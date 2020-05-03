package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class view_report extends AppCompatActivity {

    private String key, log= "edit_review", getName, getDate, getFilm, uid, getStatus;
    private TextView tv_name,tv_date,tv_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        key = getIntent().getExtras().getString("reviewId");
        tv_date= findViewById(R.id.tv_date_report);
        tv_name = findViewById(R.id.tv_name_report);
        tv_view = findViewById(R.id.tv_view_report);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_delete_report);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alBuilder = new AlertDialog.Builder(view_report.this);
                alBuilder.setMessage("Are tou sure want to delete it ?");
                alBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Delete();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alBuilder.show();
            }
        });
    }

    private void Delete() {
        FirebaseDatabase.getInstance().getReference().child("Report").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getKey = dataSnapshot.child("reviewId").getValue().toString();
                        FirebaseDatabase.getInstance().getReference().child("Review").child(getKey).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                        Toast.makeText(view_report.this, "Delete success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(view_report.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Report").child(key).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(view_report.this, "Delete success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view_report.this, "Delete failed", Toast.LENGTH_SHORT).show();
                Toast.makeText(view_report.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setView();
    }

    private void setView() {
        FirebaseDatabase.getInstance().getReference().child("Review").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getReview = dataSnapshot.child("review").getValue().toString();
                        String rating = dataSnapshot.child("rating").getValue().toString();
                        float getRating = Float.parseFloat(rating);
                        getName = dataSnapshot.child("name").getValue().toString();
                        getFilm = dataSnapshot.child("film").getValue().toString();
                        getDate = dataSnapshot.child("date").getValue().toString();
                        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        getStatus = dataSnapshot.child("edited").getValue().toString();

                        tv_date.setText(getDate);
                        tv_name.setText(getName);
                        tv_view.setText(getReview);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(view_report.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(log, databaseError.getMessage());
                    }

                });
    }
}
