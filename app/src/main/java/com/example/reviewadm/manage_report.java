package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class manage_report extends AppCompatActivity {

    FirebaseRecyclerAdapter<Report,viewHolder> adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report);
        recyclerView = findViewById(R.id.rv_mng_rep);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.in_load);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setAdapter("Spam","start");
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_report,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.spam:
                progressBar.setVisibility(View.VISIBLE);
                setAdapter("Spam", "filter");
                recyclerView.swapAdapter(adapter,true);
                adapter.startListening();
                return true;
            case R.id.inapproriate:
                progressBar.setVisibility(View.VISIBLE);
                setAdapter("Inappropriate", "filter");
                recyclerView.swapAdapter(adapter,true);
                adapter.startListening();
                return true;
            case R.id.mislead:
                progressBar.setVisibility(View.VISIBLE);
                setAdapter("Misleading", "filter");
                recyclerView.swapAdapter(adapter,true);
                adapter.startListening();
                return true;
            case R.id.all:
                progressBar.setVisibility(View.VISIBLE);
                setAdapter("Misleading", "start");
                recyclerView.swapAdapter(adapter,true);
                adapter.startListening();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter(String type, String start){

        Query query = null;

        if (start.equals("start")){
            query = FirebaseDatabase.getInstance().getReference().child("Report");
        }else if (start.equals("filter")){
            query = FirebaseDatabase.getInstance().getReference().child("Report").orderByChild("reason").equalTo(type);
        }else {
            Toast.makeText(this, "Failed to load the item", Toast.LENGTH_SHORT).show();
        }

        FirebaseRecyclerOptions<Report> options = new FirebaseRecyclerOptions.Builder<Report>()
        .setQuery(query,Report.class).build();

        adapter = new FirebaseRecyclerAdapter<Report, viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, final int position, @NonNull Report model) {
                progressBar.setVisibility(View.GONE);
                final String key = getRef(position).getKey();

                holder.setName(model.getName());
                holder.setDate(model.getDate());
                holder.setView(model.getReview());
                holder.setIv(key);

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder aBuilder = new AlertDialog.Builder(manage_report.this);
                        aBuilder.setTitle("Dismiss Report");
                        aBuilder.setMessage("Dismiss this report ?");
                        aBuilder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteReport(key);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        aBuilder.show();
                    }
                });

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteReview(key);
                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.view_report, parent, false);
                return new viewHolder(view);
            }
        };

    }

    private void deleteReview(final String key) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(manage_report.this);
        alBuilder.setTitle("Delete Review");
        alBuilder.setMessage("Are tou sure want to delete review related to it ?");

        alBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("Report").child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String getKey = dataSnapshot.child("reviewId").getValue().toString();
                                String getType = dataSnapshot.child("reason").getValue().toString();
                                String getUid = dataSnapshot.child("uid").getValue().toString();
                                deletePerma(getKey,getType, getUid);
                                //deleteReport(key);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(manage_report.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alBuilder.show();
    }

    private void deleteReport(String key) {
        FirebaseDatabase.getInstance().getReference().child("Report").child(key).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(manage_report.this, "Delete success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error_report",e.getMessage());
            }
        });
    }

    private void deletePerma(String getKey, final String reason, String getUid) {
        String reasonq = "This review has been reported as " + reason;
        String newStatus = getUid + "_banned";

        FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).child("review").setValue(reasonq)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //deleteReport(key);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(manage_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).child("status").setValue(newStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //deleteReport(key);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(manage_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).child("name").setValue("deleted")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //deleteReport(key);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(manage_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).child("rating").setValue("0.0")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //deleteReport(key);
                        Toast.makeText(manage_report.this, "Action success", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(manage_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        public View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setDate(String date) {
            TextView textView = view.findViewById(R.id.tv_date_review);
            //textView2.setVisibility(View.GONE);
            textView.setText(date);
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.tv_name_review);
            textView.setText(name);
        }

        public void setView(String reviewId) {
            TextView textView2 = view.findViewById(R.id.tv_view_review);
            textView2.setText(reviewId);
        }

        public void setIv(final String reviewId) {
            ImageButton textView2 = view.findViewById(R.id.iv_rating_review);
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteReview(reviewId);
                }
            });
        }
    }
}
