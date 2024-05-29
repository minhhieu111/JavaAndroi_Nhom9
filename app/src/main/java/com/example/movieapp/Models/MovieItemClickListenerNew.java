package com.example.movieapp.Models;

import android.widget.ImageView;

public interface MovieItemClickListenerNew {
    void onMovieClick(GetVideoDetails getVideoDetails , ImageView imageView);
    void onMovieClick(SliderSide sliderSide , ImageView imageView);
}
