package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    FirebaseAuth auth;
    AlertDialog.Builder alerBuilder;
    ProgressDialog progressDialog;
    EditText et_pass, et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.tie_email_login);
        et_pass = findViewById(R.id.tie_pass_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button btn_register = findViewById(R.id.btn_register);
        progressDialog = new ProgressDialog(login.this);
        Button btn_login = findViewById(R.id.btn_login);
        Button btn_forget = findViewById(R.id.btn_forget);
        alerBuilder = new AlertDialog.Builder(this);
        auth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( login.this, forget.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getPass = et_pass.getText().toString();
                String getEmail = et_email.getText().toString();

                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                if (getEmail.isEmpty() || getPass.isEmpty()){
                    Toast.makeText(login.this, "Please fill the field", Toast.LENGTH_SHORT).show();

                }else{
                    sendLogin(getEmail, getPass);
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( login.this, register.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAcc();
    }

    private void checkAcc(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user !=null){
            startActivity(new Intent(login.this, home.class));
        }else {
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendLogin(String email, String pass){
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //startActivity(new Intent(login.this, home.class));
                            checkLevel();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLevel() {
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getLevel = dataSnapshot.child("level").getValue().toString();
                if (getLevel.equals("admin")){
                    startActivity(new Intent(login.this, home.class));
                    //sendLogin(getEmail,getPass);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Account is invalid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK){
            alerBuilder.setTitle("Exit")
                    .setMessage("Are you sure ?")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alerBuilder.show();
        }

        return super.onKeyDown(keyCode, event);
    }
}
