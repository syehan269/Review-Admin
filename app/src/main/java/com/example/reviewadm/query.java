package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class query extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Review, viewHolder> adapter;
    private String key;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        progressBar = findViewById(R.id.in_que);
        recyclerView = findViewById(R.id.rv_query);
        key  = getIntent().getExtras().getString("id_search");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Toast.makeText(query.this, key, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setView();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setView() {
        final Query query = FirebaseDatabase.getInstance().getReference().child("Review").orderByChild("film").equalTo(key);
        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Review, viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Review model) {

                progressBar.setVisibility(View.GONE);
                holder.setRating(model.getRating());
                holder.setName(model.getName());
                holder.setDate(model.getDate());
                holder.setReview(model.getReview());
                toolbar.setTitle(key);

                final String getKey = getRef(position).getKey();
                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(getBaseContext());
                        aleBuilder.setTitle("Delete Item")
                                .setMessage("Are you sure want to delete this item ?");
                        aleBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(query.this, "Delete success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(query.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                View view = getLayoutInflater().inflate(R.layout.view_review_list, parent, false);
                return new viewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private class viewHolder extends RecyclerView.ViewHolder {
        View view;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view  =itemView;
        }

        public void setReview(String review) {
            TextView textView = view.findViewById(R.id.tv_view_review);
            textView.setText(review);
        }

        public void setRating(String rating) {
            TextView textView = view.findViewById(R.id.tv_rating_review);
            textView.setText(rating);
        }

        public void setDate(String date) {
            TextView textView = view.findViewById(R.id.tv_date_review);
            textView.setText(date);
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.tv_name_review);
            textView.setText(name);
        }
    }
}
