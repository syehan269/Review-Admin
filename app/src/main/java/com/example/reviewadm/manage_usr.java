package com.example.reviewadm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class manage_usr extends AppCompatActivity {

    private RecyclerView rv_mng_usr;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<User, viewHolder> firebaseRecyclerAdapter;
    private ProgressBar loading_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_usr);

        rv_mng_usr = findViewById(R.id.rv_mng_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        loading_in = findViewById(R.id.in_load);
        reference = FirebaseDatabase.getInstance().getReference("User");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage user");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rv_mng_usr.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
        loading_in.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        setListUser();
        super.onStart();
        loading_in.setVisibility(View.VISIBLE);
        firebaseRecyclerAdapter.startListening();
    }

    private void setListUser(){
        Query query = reference.orderByPriority();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, viewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull final User model) {
                loading_in.setVisibility(View.GONE);
                holder.setName(model);
                holder.setLevel(model);
                holder.setEmail(model);

                String key = getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(manage_usr.this, model.getPassword(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mng_usr, parent, false);

                return new viewHolder(view);
            }
        };

        rv_mng_usr.setAdapter(firebaseRecyclerAdapter);
    }


    public static class viewHolder extends RecyclerView.ViewHolder {

        View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void setLevel(User level){
            TextView et_level = view.findViewById(R.id.tv_level_usr);
            String getLevel = level.getLevel();
            et_level.setText(getLevel);
        }

        private void setName(User name){
            TextView et_level = view.findViewById(R.id.tv_name_usr);
            String username = name.getname();
            et_level.setText(username);
        }

        private void setEmail(User email){
            TextView et_level = view.findViewById(R.id.tv_email_usr);
            String getEmail = email.getEmail();
            et_level.setText(getEmail);
        }

        private void setPass(User pass){
            String getPass = pass.getPassword();
        }

    }
}
