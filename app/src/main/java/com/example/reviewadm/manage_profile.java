package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class manage_profile extends AppCompatActivity {

    private TextView tv_name, tv_pass, tv_email;
    private Button btn_delete;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RelativeLayout rl_email, rl_pass, rl_name;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        tv_email = findViewById(R.id.tv_email_profile);
        tv_name = findViewById(R.id.tv_name_profile);
        tv_pass = findViewById(R.id.tv_pass_profile);
        btn_delete = findViewById(R.id.btn_delete_profile);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        rl_email = findViewById(R.id.rl_email);
        rl_name = findViewById(R.id.rl_name);
        rl_pass = findViewById(R.id.rl_pass);
        progressDialog = new ProgressDialog(manage_profile.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        rl_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogEdit("email");
            }
        });

        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogEdit("name");
            }
        });

        rl_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_profile.this,update_pas.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserData();
    }

    private void setDialogEdit(final String type){
        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(manage_profile.this);
        View layoutInflater = LayoutInflater.from(manage_profile.this).inflate(R.layout.view_dialog_edit, null);
        final EditText editText = layoutInflater.findViewById(R.id.et_edit_dialog);
        editText.setHint("Enter "+type);
        aleBuilder.setView(layoutInflater);

        aleBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updateString = editText.getText().toString();

                if (type.equals("name")){
                    tv_name.setText(updateString);
                }else if (type.equals("email")){
                    tv_email.setText(updateString);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        aleBuilder.show();
    }

    private void setUserData() {
        databaseReference.child("User").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getName = dataSnapshot.child("name").getValue().toString();
                String getPass = dataSnapshot.child("pass").getValue().toString();
                String getEmail = dataSnapshot.child("email").getValue().toString();

                tv_email.setText(getEmail);
                tv_name.setText(getName);
                tv_pass.setText(getPass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(manage_profile.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(manage_profile.this);
        aleBuilder.setTitle("Delete Account")
                .setMessage("Are you sure want to delete this account ?");

        aleBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setProgressDialog();
                databaseReference.child("User").child(auth.getUid()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(manage_profile.this, "delete db success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(manage_profile.this, "delete db failed", Toast.LENGTH_SHORT).show();
                    }
                });

                auth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(manage_profile.this, "delete auth success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(manage_profile.this, "delete failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        aleBuilder.show();
    }

    private void setProgressDialog() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.update_profile:
                updateData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        setProgressDialog();

        final String getEmail = tv_email.getText().toString();
        final String getName = tv_name.getText().toString();
        final String getPass = tv_pass.getText().toString();

        try {
            databaseReference.child("User").child(auth.getUid()).child("email").setValue(getEmail);
            databaseReference.child("User").child(auth.getUid()).child("name").setValue(getName);
            Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show();

            auth.getCurrentUser().updateEmail(getEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    //Toast.makeText(manage_profile.this, "email success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    //Toast.makeText(manage_profile.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
