package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class create_film extends AppCompatActivity {

    private EditText et_title, et_sinopsis;
    private Spinner spn_genre;
    private TextView tv_date;
    private String getDate, ImgName, alamat;
    private ProgressDialog progressDialog;
    private Button btn_upload;
    private int IMAGE_REQ = 11;
    private Uri filePath;
    private String downloadUrl;
    private ImageView iv_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_film);

        Toolbar toolbar = findViewById(R.id.toolbar);
        spn_genre = findViewById(R.id.spn_genre);
        tv_date = findViewById(R.id.tv_date1);
        et_sinopsis = findViewById(R.id.et_sinopsis);
        et_title = findViewById(R.id.et_title);
        btn_upload = findViewById(R.id.btn_upload_img);
        iv_preview = findViewById(R.id.iv_preview_film);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int mYear =calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(create_film.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        getDate = dayOfMonth+"/"+(month+1)+"/"+year;
                        tv_date.setText(getDate);
                    }
                },mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImg();
            }
        });

    }

    @Override
    protected void onStart() {
        getSpinnerAdapter();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opsi_create, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.submit_film:
                showProgress();
                uploadImg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadImg(){
        if (filePath != null){

            String getTitle = et_title.getText().toString().trim();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_");
            Date date = new Date();
            ImgName = dateFormat.format(date);

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PreviewImg").child(ImgName+getTitle+".jpg");

            storageReference.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        downloadUrl = task.getResult().toString();
                        //Toast.makeText(create_film.this, downloadUrl, Toast.LENGTH_SHORT).show();
                        submitFilm(downloadUrl);
                    }else {
                        Toast.makeText(create_film.this, "error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(create_film.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void selectImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"),IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Toast.makeText(this, "image set", Toast.LENGTH_SHORT).show();
                iv_preview.setBackgroundColor(Color.WHITE);
                iv_preview.setImageBitmap(bitmap);
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(create_film.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void submitFilm(String kek) {

        final String getGenre = spn_genre.getSelectedItem().toString();
        final String getTitle = et_title.getText().toString().trim();
        final String getSinopsis = et_sinopsis.getText().toString().trim();

        Film film = new Film(getTitle, getGenre, getDate, getSinopsis, kek);

        FirebaseDatabase.getInstance().getReference().child("Film").push().setValue(film)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(create_film.this, "Upload success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(create_film.this, manage_film.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(create_film.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(create_film.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getSpinnerAdapter() {
        FirebaseDatabase.getInstance().getReference().child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> category = new ArrayList<String>();

                        for (DataSnapshot genreSnapshot: dataSnapshot.getChildren()){
                            String areaNAme = genreSnapshot.child("genre").getValue(String.class);
                            category.add(areaNAme);
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(create_film.this, R.layout.support_simple_spinner_dropdown_item, category);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spn_genre.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
