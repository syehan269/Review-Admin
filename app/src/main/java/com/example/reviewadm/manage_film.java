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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class manage_film extends AppCompatActivity {

    private FloatingActionButton fab_mng_film;
    private RecyclerView rv_film;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<Film, viewHolderFilm> adapter;
    private ProgressBar loading_ind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_film);

        fab_mng_film = findViewById(R.id.fab_mng_film);
        Toolbar toolbar = findViewById(R.id.toolbar);
        rv_film = findViewById(R.id.rv_mng_film);
        loading_ind = findViewById(R.id.in_load);

        //reference = FirebaseDatabase.getInstance().getReference("Film");
        reference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        rv_film.setLayoutManager(linearLayoutManager);

        fab_mng_film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(manage_film.this, create_film.class));
            }
        });

    }

    @Override
    protected void onStart() {

        setViewFilm();
        super.onStart();

        if (adapter == null){
            Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
            loading_ind.setVisibility(View.GONE);
            //adapter.stopListening();
        }else {
            loading_ind.setVisibility(View.VISIBLE);
            adapter.startListening();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        loading_ind.setVisibility(View.GONE);
    }

    private void setViewFilm() {
        Query query = reference.child("Film").orderByPriority();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Film>()
                .setQuery(query, Film.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Film, viewHolderFilm>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolderFilm holder, int position, @NonNull Film model) {
                loading_ind.setVisibility(View.GONE);

                holder.setDate(model);
                holder.setGenre(model);
                holder.setTitle(model);
                holder.setImg(model);

                final String key = getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(manage_film.this, view_film.class);
                        intent.putExtra("id_film",key);
                        startActivity(intent);
                    }
                });

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteFilm(key);
                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public viewHolderFilm onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mng_film, parent, false);

                return new viewHolderFilm(view);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(manage_film.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        rv_film.setAdapter(adapter);

    }

    private void deleteFilm(final String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(manage_film.this);
        alertDialog.setTitle("Delete Film");
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("Film").child(key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(manage_film.this, "Delete success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(manage_film.this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private class viewHolderFilm extends RecyclerView.ViewHolder {
        private View view;

        public viewHolderFilm(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setTitle(Film title){
            TextView et_1 = view.findViewById(R.id.tv_title_film);
            String getTitle = title.getTitle();
            et_1.setText(getTitle);
        }

        private void setGenre(Film genre){
            TextView et_2 = view.findViewById(R.id.tv_genre_film);
            String getGenre = genre.getGenre();
            et_2.setText(getGenre);
        }

        private void setDate(Film rilis){
            TextView et_3 = view.findViewById(R.id.tv_date_film);
            String getDate = rilis.getRilis();
            et_3.setText(getDate);
        }

        private void setImg(Film alamat){
            ImageView iv = view.findViewById(R.id.iv_list_film);
            Glide.with(manage_film.this).load(alamat.getAlamat()).into(iv);
        }

    }

}
