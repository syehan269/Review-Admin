package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class update_pas extends AppCompatActivity {

    TextInputEditText et_old, et_con, et_new;
    Button btn_submit;
    String email,name;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        et_con  = findViewById(R.id.tie_con);
        et_new = findViewById(R.id.tie_new);
        et_old = findViewById(R.id.tie_old);
        btn_submit = findViewById(R.id.btn_pass);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePass();
            }
        });

    }

    private void updatePass() {
        String passNew = et_new.getText().toString();
        String passCon = et_con.getText().toString();

        if (passCon.equals(passNew)){
            progressDialog = new ProgressDialog(update_pas.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            updateDb(passCon);
        }else {
            Toast.makeText(this, "Password false", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDb(final String passCon) {

        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getUid()).child("pass")
                .setValue(passCon).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateAuth(passCon);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(update_pas.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAuth(String passCon) {
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(passCon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(update_pas.this, "Update password success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(update_pas.this,manage_profile.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(update_pas.this, "error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOld();
        et_old.setKeyListener(null);
    }

    private void setOld() {
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getPass = dataSnapshot.child("pass").getValue().toString();
                        et_old.setText(getPass);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(update_pas.this, "error when retrieving password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
