package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class manage_gnr extends AppCompatActivity {

    private FloatingActionButton fab_gnr;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private AlertDialog.Builder aleBuilder;
    private FirebaseRecyclerAdapter<Genre, ViewHolderGnr> adapter;
    private String[] colorGenre = new String[]{"RED","BLUE","BLACK","YELLOW","GREEN"};
    private String logCode, downloadUrl;
    private int IMAGE_REQ = 11;
    private ProgressBar loading_in;
    private ArrayAdapter adapterSPN;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gnr);

        fab_gnr = findViewById(R.id.fab_mng_gnr);
        recyclerView = findViewById(R.id.rv_mng_gnr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        aleBuilder = new AlertDialog.Builder(this);
        reference = FirebaseDatabase.getInstance().getReference();
        logCode = "GNR ";
        loading_in = findViewById(R.id.in_load);
        adapterSPN = new ArrayAdapter<String>(manage_gnr.this, android.R.layout.simple_spinner_dropdown_item, colorGenre);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage genre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab_gnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(manage_gnr.this, create_genre.class));
            }
        });

    }

    @Override
    protected void onStart() {
        setGenreView();
        super.onStart();
        loading_in.setVisibility(View.VISIBLE);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loading_in.setVisibility(View.GONE);
        adapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Toast.makeText(this, "image set", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void setGenreView(){
        Query query1 = reference.child("Category").orderByPriority();

        FirebaseRecyclerOptions<Genre> options =
                new FirebaseRecyclerOptions.Builder<Genre>()
                .setQuery(query1, Genre.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Genre, ViewHolderGnr>(options) {
            @NonNull
            @Override
            public ViewHolderGnr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gnr, parent, false);
                return new ViewHolderGnr(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolderGnr holder, int position, @NonNull Genre model) {
                holder.setGenreTv(model);
                loading_in.setVisibility(View.GONE);

                final String key = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateGenre(key);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteGenre(key);
                        return false;
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }

    private class ViewHolderGnr extends RecyclerView.ViewHolder {

        private TextView tv_genre;

        public ViewHolderGnr(@NonNull View itemView) {
            super(itemView);
            tv_genre = itemView.findViewById(R.id.tv_genre);
        }

        void setGenreTv(Genre genre){
            String genreText = genre.getGenre();
            tv_genre.setText(genreText);
        }

    }

    private void updateGenre(final String key){

        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(manage_gnr.this);
        aleBuilder.setTitle("Update Genre");

        final View view = LayoutInflater.from(manage_gnr.this).inflate(R.layout.view_dialog, null );
        aleBuilder.setView(view);
        final EditText editText = view.findViewById(R.id.et_edit_dial);
        final TextView textView = view.findViewById(R.id.tv_old_genre);

        FirebaseDatabase.getInstance().getReference().child("Category").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String getGenre = dataSnapshot.child("genre").getValue().toString();
                        //editText.setText(getGenre);
                        textView.setText(getGenre);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(manage_gnr.this, "error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        aleBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String getUpdate = editText.getText().toString().trim();
                //Genre genre = new Genre(getUpdate, downloadUrl);

                reference.child("Category").child(key).child("genre").setValue(getUpdate).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(manage_gnr.this, "Genre updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(manage_gnr.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void deleteGenre(final String key){

        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(manage_gnr.this);

        aleBuilder.setTitle("Delete Genre");
        aleBuilder.setMessage("Are you sure ?");
        aleBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("Category").child(key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(manage_gnr.this, "Delete success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(manage_gnr.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

}
