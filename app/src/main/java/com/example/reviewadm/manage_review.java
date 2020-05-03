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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.zip.Inflater;

public class manage_review extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseRecyclerAdapter<Review, viewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_review);
        recyclerView = findViewById(R.id.rv_mng_rvw);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.in_rvw);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_mng_rvw);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuery();
            }
        });

    }

    private void getQuery() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_edit,null,false);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = view.findViewById(R.id.et_edit_dialog);
                String edit = editText.getText().toString().trim();

                Intent intent = new Intent(manage_review.this, query.class);
                intent.putExtra("id_search",edit);
                startActivity(intent);
                Toast.makeText(manage_review.this, edit, Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        setView();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setView() {
        progressBar.setVisibility(View.GONE);
        Query query = FirebaseDatabase.getInstance().getReference().child("Review");
        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Review, viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Review model) {
                holder.setName(model.getName());
                holder.setRating(model.getRating());
                holder.setReview(model.getReview());
                holder.setDate(model.getDate());

                final String getKey = getRef(position).getKey();
                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(manage_review.this);
                        aleBuilder.setTitle("Delete Item")
                                .setMessage("Are you sure want to delete this item ?");
                        aleBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(manage_review.this, "Delete success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(manage_review.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        aleBuilder.show();

                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.view_review_list,parent,false);
                return new viewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        public View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setDate(String date) {
            TextView textView = view.findViewById(R.id.tv_date_review);
            textView.setText(date);
        }

        public void setReview(String review) {
            TextView textView = view.findViewById(R.id.tv_view_review);
            textView.setText(review);
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.tv_name_review);
            textView.setText(name);
        }

        public void setRating(String rating) {
            TextView textView = view.findViewById(R.id.tv_rating_review);
            textView.setText(rating);
        }
    }
}
