package com.example.movieapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.movieapp.Adapter.MovieShowAdapter;
import com.example.movieapp.Models.GetVideoDetails;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Models.MovieItemClickListenerNew;
import com.example.movieapp.Models.SliderSide;
import com.example.movieapp.R;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements MovieItemClickListenerNew {
    private SearchView searchView;
    private RecyclerView Movies;
    private MovieShowAdapter movieShowAdapter;
    private List<GetVideoDetails> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniSearchBar();
        movies = (List<GetVideoDetails>) getIntent().getSerializableExtra("allMovies");
        iniMoviesList();
    }

    private void iniMoviesList(){
        Movies = findViewById(R.id.movies);
        movieShowAdapter = new MovieShowAdapter(this,movies,this);
        Movies.setAdapter(movieShowAdapter);
        Movies.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void iniSearchBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.search_bar);

            searchView = findViewById(R.id.searchView);

            searchView.setIconified(false);

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    finish();
                    return false;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    movieShowAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    movieShowAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public void onMovieClick(GetVideoDetails movie, ImageView imageView) {
        Intent in = new Intent(this, MovieDetailsActivity.class);
        in.putExtra("title",movie.getVideo_name());
        in.putExtra("imgURL",movie.getVideo_thumb());
        in.putExtra("imgCover",movie.getVideo_thumb());
        in.putExtra("movieDetail",movie.getVideo_description());
        in.putExtra("movieUrl",movie.getVideo_url());
        in.putExtra("movieCategory",movie.getVideo_category());
        startActivity(in);
    }

    @Override
    public void onMovieClick(SliderSide sliderSide, ImageView imageView) {}
}
