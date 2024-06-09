package com.example.movieapp.User;

import android.app.ActivityOptions;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.Adapter.MovieShowAdapter;
import com.example.movieapp.Models.GetVideoDetails;
import com.example.movieapp.Models.MovieItemClickListenerNew;
import com.example.movieapp.Models.SliderSide;
import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity implements MovieItemClickListenerNew {
    private ImageView MoviesThumbNail, MoviesCoverImg;
    TextView tv_title, tv_descrition, actionbar_title;
    ImageView btn_back, btn_search;
    FloatingActionButton play_fab;
    RecyclerView recyclerViewSimilarMovies;
    MovieShowAdapter movieShowAdapter;
    DatabaseReference mDatabasereferance;
    List<GetVideoDetails> uploads, actionsMovies, sportMovies, comedyMovies, romanticMovies, adventureMovies;
    String current_video_url, current_video_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_detail);
            actionbar_title = actionBar.getCustomView().findViewById(R.id.movie_title);
            btn_back = actionBar.getCustomView().findViewById(R.id.btn_back);
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inView();
        similarMoviesRecycler();
        similarMovies();

        play_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, MoviePlayerActivity.class);
                intent.putExtra("videoUri",current_video_url);
                startActivity(intent);
            }
        });
    }

    private void inView() {
        play_fab = findViewById(R.id.play_fab);
        tv_title = findViewById(R.id.detail_movies_title);
        tv_descrition = findViewById(R.id.detail_movies_desc);
        MoviesThumbNail = findViewById(R.id.detail_movies_img);
        MoviesCoverImg = findViewById(R.id.detail_movies_cover);
        recyclerViewSimilarMovies = findViewById(R.id.recycler_similar_movies);

        String movieTitle = getIntent().getExtras().getString("title");
        String imgrecoresId = getIntent().getExtras().getString("imgURL");
        String imageCover = getIntent().getExtras().getString("imgCover");
        String moviesDetailstext = getIntent().getExtras().getString("movieDetail");
        String moviesUrl = getIntent().getExtras().getString("movieUrl");
        String moviesCategory = getIntent().getExtras().getString("movieCategory");
        current_video_url = moviesUrl;
        current_video_category = moviesCategory;
        Glide.with(this).load(imgrecoresId).into(MoviesThumbNail);
        Glide.with(this).load(imageCover).into(MoviesCoverImg);
        tv_title.setText(movieTitle);
        tv_descrition.setText(moviesDetailstext);
        actionbar_title.setText(movieTitle);;
    }

    private void similarMoviesRecycler() {
        uploads = new ArrayList<>();
        sportMovies = new ArrayList<>();
        comedyMovies = new ArrayList<>();
        romanticMovies = new ArrayList<>();
        adventureMovies = new ArrayList<>();
        actionsMovies = new ArrayList<>();

        mDatabasereferance = FirebaseDatabase.getInstance().getReference("videos");
        mDatabasereferance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                    GetVideoDetails upload = postsnapshot.getValue(GetVideoDetails.class);
                    if (upload.getVideo_category().equals("Action")) {
                        actionsMovies.add(upload);
                    }
                    if (upload.getVideo_category().equals("Sport")) {
                        sportMovies.add(upload);
                    }
                    if (upload.getVideo_category().equals("Adventure")) {
                        adventureMovies.add(upload);
                    }
                    if (upload.getVideo_category().equals("Comedy")) {
                        comedyMovies.add(upload);
                    }
                    if (upload.getVideo_category().equals("Romantic")) {
                        romanticMovies.add(upload);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void similarMovies() {
        if (current_video_category.equals("Action")) {
            movieShowAdapter = new MovieShowAdapter(this, actionsMovies,this);
            recyclerViewSimilarMovies.setAdapter(movieShowAdapter);
            recyclerViewSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
        if (current_video_category.equals("Sport")) {
            movieShowAdapter = new MovieShowAdapter(this, sportMovies,this);
            recyclerViewSimilarMovies.setAdapter(movieShowAdapter);
            recyclerViewSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
        if (current_video_category.equals("Adventure")) {
            movieShowAdapter = new MovieShowAdapter(this, adventureMovies,this);
            recyclerViewSimilarMovies.setAdapter(movieShowAdapter);
            recyclerViewSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
        if (current_video_category.equals("Comedy")) {
            movieShowAdapter = new MovieShowAdapter(this, comedyMovies,this);
            recyclerViewSimilarMovies.setAdapter(movieShowAdapter);
            recyclerViewSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
        if (current_video_category.equals("Romantic")) {
            movieShowAdapter = new MovieShowAdapter(this, romanticMovies,this);
            recyclerViewSimilarMovies.setAdapter(movieShowAdapter);
            recyclerViewSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            movieShowAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMovieClick(GetVideoDetails movie, ImageView imageView) {
        tv_title.setText(movie.getVideo_name());
        actionbar_title.setText(movie.getVideo_name());
        Glide.with(this).load(movie.getVideo_thumb()).into(MoviesThumbNail);
        Glide.with(this).load(movie.getVideo_thumb()).into(MoviesCoverImg);
        tv_descrition.setText(movie.getVideo_description());
        current_video_url = movie.getVideo_url();
        current_video_category = movie.getVideo_category();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MovieDetailsActivity.this, imageView, "sharedName");
        options.toBundle();
    }

    @Override
    public void onMovieClick(SliderSide sliderSide, ImageView imageView) {

    }
}