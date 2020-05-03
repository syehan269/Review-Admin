package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class view_film extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView tv_title, tv_genre, tv_sinopsis, tv_rilis;
    private ImageView iv_img;
    private String key;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_film);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getExtras().getString("id_film");
        databaseReference = FirebaseDatabase.getInstance().getReference("Film").child(key);
        tv_genre = findViewById(R.id.tv_genre_view);
        tv_rilis = findViewById(R.id.tv_rilis_view);
        tv_sinopsis = findViewById(R.id.tv_sinopsis_view);
        tv_title = findViewById(R.id.tv_title_view);
        iv_img = findViewById(R.id.iv_film_view);
        fab = findViewById(R.id.fab_edit);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_film.this, update_film.class);
                intent.putExtra("id_film", key);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getFilmData();
    }

    private void getFilmData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getTitle = dataSnapshot.child("title").getValue().toString();
                String getDate = dataSnapshot.child("rilis").getValue().toString();
                String getGenre = dataSnapshot.child("genre").getValue().toString();
                String getSinopsis = dataSnapshot.child("sinopsis").getValue().toString();
                String getImage = dataSnapshot.child("alamat").getValue().toString();

                tv_genre.setText(getGenre);
                tv_rilis.setText(getDate);
                tv_sinopsis.setText(getSinopsis);
                tv_title.setText(getTitle);
                iv_img.setBackgroundColor(Color.WHITE);
                Glide.with(view_film.this).load(getImage).into(iv_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view_film.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
