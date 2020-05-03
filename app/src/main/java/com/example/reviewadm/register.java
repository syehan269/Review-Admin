package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private EditText et_name, et_email, et_pass;
    private Button btn_send;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog proBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        et_email = findViewById(R.id.tie_email_register);
        et_name = findViewById(R.id.tie_name_register);
        et_pass = findViewById(R.id.tie_pass_register);
        btn_send = findViewById(R.id.btn_send_register);
        auth = FirebaseAuth.getInstance();
        proBuilder = new ProgressDialog(this, R.style.ThemeOverlay_MaterialComponents_Dialog);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proBuilder.setMessage("Please wait...");
                proBuilder.show();
                String getEmail = et_email.getText().toString();
                String getPass = et_pass.getText().toString();
                String getName = et_name.getText().toString();

                createUser(getEmail, getPass, getName);
            }
        });

    }

    private void createUser(final String email, final String pass, final String name){
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    proBuilder.dismiss();
                    FirebaseUser user = auth.getCurrentUser();

                    getData(user.getUid(), email, pass, name);
                    Toast.makeText(register.this, "Create account successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent( register.this, login.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                proBuilder.dismiss();
                Toast.makeText(register.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String userID, String getPass, String getEmail, String getName){
        reference = FirebaseDatabase.getInstance().getReference();
        String type = "user";
        User userData = new User(getName, userID, getEmail, getPass, "user");

        reference.child("User").child(userID).setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(register.this, "User data created", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(register.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
