package com.example.movieapp.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.movieapp.Models.HelperClass;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private TextView fullnameTV, emailTV, dobTV, genderTV;
    private FirebaseAuth mAuth;
    private String fullname, email, dob, gender;
    ImageView accountImage, backBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniActionBar();

        fullnameTV = findViewById(R.id.fullname);
        emailTV = findViewById(R.id.email);
        dobTV = findViewById(R.id.dob);
        genderTV = findViewById(R.id.gender);
        accountImage = findViewById(R.id.img_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            showUserProfiles(user);
        }else{
            Toast.makeText(this, "User detail in not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_profile);

            backBtn = findViewById(R.id.btn_back);
            logoutBtn = findViewById(R.id.btn_logout);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
                }
            });

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                }
            });
        }
    }

    private void showUserProfiles(FirebaseUser user) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String uid = user.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
        referenceProfile.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClass helperClass = snapshot.getValue(HelperClass.class);

                if(helperClass != null){
                    fullname = helperClass.getUsername();
                    email = helperClass.getEmail();
                    dob = helperClass.getDob();
                    gender = helperClass.getGender();

                    fullnameTV.setText(fullname);
                    emailTV.setText(email);
                    dobTV.setText(dob);
                    genderTV.setText(gender);
                    Glide.with(UserProfileActivity.this).load(helperClass.getImg()).into(accountImage);
                }
                progressDialog.dismiss();

                accountImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfileActivity.this, ProfileImageActivity.class);
                        intent.putExtra("imageUri", helperClass.getImg());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}