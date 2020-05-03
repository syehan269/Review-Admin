package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class update_film extends AppCompatActivity {

    private EditText et_title;
    private ImageView iv_film;
    private TextView tv_date;
    private Spinner spn_genre;
    private TextInputEditText et_sinopsis;
    private DatabaseReference databaseReference;
    private String key, getDate, ImgName,downUri;
    private Uri uri;
    private ProgressDialog progressDialog;
    private int IMAGE_REQ = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_film);

        Toolbar toolbar= findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(update_film.this);
        key = getIntent().getExtras().getString("id_film");
        iv_film = findViewById(R.id.iv_update_film_adm);
        tv_date = findViewById(R.id.et_date_update);
        spn_genre = findViewById(R.id.spn_genre_update);
        et_sinopsis = findViewById(R.id.et_sinopsis_update);
        et_title = findViewById(R.id.et_title_update);
        databaseReference = FirebaseDatabase.getInstance().getReference("Film");
        FloatingActionButton fab_update = findViewById(R.id.fab_update_film);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                updateImg();
            }
        });

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
        
        iv_film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"),IMAGE_REQ);
    }

    private void showDate() {
        final Calendar calendar = Calendar.getInstance();
        int mYear =calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(update_film.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                getDate = dayOfMonth+"/"+(month+1)+"/"+year;
                tv_date.setText(getDate);
            }
        },mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setViewUpdate();
        setSpinnerAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSpinnerAdapter() {
        FirebaseDatabase.getInstance().getReference().child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> category = new ArrayList<String>();

                        for (DataSnapshot genreSnapshot: dataSnapshot.getChildren()){
                            String areaNAme = genreSnapshot.child("genre").getValue(String.class);
                            category.add(areaNAme);
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(update_film.this, R.layout.support_simple_spinner_dropdown_item, category);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spn_genre.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setViewUpdate() {
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getTitle = dataSnapshot.child("title").getValue().toString();
                String getDate = dataSnapshot.child("rilis").getValue().toString();
                String getGenre = dataSnapshot.child("genre").getValue().toString();
                String getSinop = dataSnapshot.child("sinopsis").getValue().toString();
                String getImg = dataSnapshot.child("alamat").getValue().toString();

                tv_date.setText(getDate);
                //spn_genre.setSelection(Integer.parseInt(getGenre));
                et_sinopsis.setText(getSinop);
                et_title.setText(getTitle);
                iv_film.setBackgroundColor(Color.WHITE);
                Glide.with(getApplicationContext()).load(getImg).into(iv_film);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(update_film.this, "error:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateImg() {
        if (uri != null){

            String getTitle = et_title.getText().toString().trim();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_");
            Date date = new Date();
            ImgName = dateFormat.format(date);

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PreviewImg").child(ImgName+getTitle+".jpg");
            storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        downUri = task.getResult().toString();
                        Toast.makeText(update_film.this, downUri, Toast.LENGTH_SHORT).show();
                        updateFilm(downUri);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(update_film.this, "error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(update_film.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else if (uri == null){
            progressDialog.dismiss();
            Toast.makeText(this, "Choose the picture", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Toast.makeText(this, "image set", Toast.LENGTH_SHORT).show();
                iv_film.setBackgroundColor(Color.WHITE);
                iv_film.setImageBitmap(bitmap);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showProgress() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void updateFilm(String downUri) {

        String updateTitle = et_title.getText().toString();
        String updateDate = tv_date.getText().toString();
        String updateSinop = et_sinopsis.getText().toString();
        String updateGenre = spn_genre.getSelectedItem().toString();

        try {
            databaseReference.child(key).child("title").setValue(updateTitle);
            databaseReference.child(key).child("rilis").setValue(updateDate);
            databaseReference.child(key).child("sinopsis").setValue(updateSinop);
            databaseReference.child(key).child("genre").setValue(updateGenre);
            databaseReference.child(key).child("alamat").setValue(downUri);

            startActivity(new Intent(update_film.this, manage_film.class));
            finish();
            progressDialog.dismiss();
            Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
