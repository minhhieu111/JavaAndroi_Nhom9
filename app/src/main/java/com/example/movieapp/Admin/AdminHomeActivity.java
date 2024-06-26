package com.example.movieapp.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Adapter.GetMovieForAdminHomeAdapter;
import com.example.movieapp.Models.GetVideoDetails;
import com.example.movieapp.R;
import com.example.movieapp.User.LoginActivity;
import com.example.movieapp.User.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GetMovieForAdminHomeAdapter videoAdapter;
    private List<GetVideoDetails> videoList;
    private DatabaseReference databaseVideos;
    private Button btnAddVideo;
    private ImageView btnLogout;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        videoAdapter = new GetMovieForAdminHomeAdapter(this, videoList);
        recyclerView.setAdapter(videoAdapter);

        builder = new AlertDialog.Builder(this);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        databaseVideos = FirebaseDatabase.getInstance().getReference("videos");

        databaseVideos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    GetVideoDetails video = postSnapshot.getValue(GetVideoDetails.class);
                    videoList.add(video);
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeActivity.this, "Failed to load videos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddVideo = findViewById(R.id.btnAddVideo);
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, UploadVideoActivity.class));
            }
        });
    }
}