package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class create_genre extends AppCompatActivity {

    private TextView tv_img;
    private TextInputEditText et_caption;
    private ImageView iv_genre;
    private MaterialButton btn_img;
    private int IMAGE_REQ = 11;
    private Uri filePath;
    private ProgressDialog progressDialog;
    private String downloadUrl, tag = "create_genre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_genre);

        Toolbar toolbar = findViewById(R.id.toolbar_genre1);
        tv_img = findViewById(R.id.tv_genre_caption);
        et_caption = findViewById(R.id.et_create_genre);
        iv_genre = findViewById(R.id.iv_genre_create);
        btn_img = findViewById(R.id.btn_img_genre);
        progressDialog = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImg();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"),IMAGE_REQ);
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
                showProggress();
                setGenreValue();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProggress() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void sendData(String getGenre, String downloadUrl) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String getDate = dateFormat.format(new Date());

        Genre genre = new Genre(getGenre, downloadUrl);
        FirebaseDatabase.getInstance().getReference().child("Category").push().setValue(genre).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                et_caption.setText(null);
                iv_genre.setBackgroundColor(getResources().getColor(R.color.back));
                iv_genre.setImageBitmap(null);
                Toast.makeText(create_genre.this, "Upload success", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(create_genre.this, "Upload failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        
    }

    private void setGenreValue() {
        final String getGenre = et_caption.getText().toString().trim();
        if (filePath != null){
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String ImgName = dateFormat.format(new Date());
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("GenreImg").child(ImgName+"_"+getGenre+".jpg");

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
                        Log.d(tag, "genre: "+getGenre);
                        Log.d(tag, "Url: "+downloadUrl);
                        sendData(getGenre, downloadUrl);
                        //progressDialog.dismiss();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(create_genre.this, "error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(create_genre.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }else {
            Toast.makeText(this, "Filepath null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Glide.with(getBaseContext()).load(bitmap).into(iv_genre);
                //Toast.makeText(this, "image set", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
