package com.adarsh.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.adarsh.popularmovies.utilities.Constants;
import com.adarsh.popularmovies.utilities.MovieData;
import com.adarsh.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieNameTextView;

    private TextView mMovieDateTextView;

    private TextView mMovieRatingTextView;

    private ImageView mMovieCoverImageView;

    private TextView mMovieDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(R.string.movie_detail);
        }

        mMovieNameTextView = findViewById(R.id.tv_movie_name);
        mMovieDateTextView = findViewById(R.id.tv_movie_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        mMovieCoverImageView = findViewById(R.id.iv_movie);
        mMovieDescriptionTextView = findViewById(R.id.tv_movie_description);

        if (getIntent().getExtras() != null) {
            populateUI(getIntent().getExtras());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUI(Bundle savedInstanceState) {
        MovieData movieData = (MovieData) savedInstanceState.getParcelable(Constants.MOVIE_DATA);
        if (movieData != null) {
            mMovieNameTextView.setText(movieData.getTitle());
            mMovieDateTextView.setText(movieData.getRelease_date());
            mMovieRatingTextView.setText(String.valueOf(movieData.getVoteAverage()));
            String imageUrl = NetworkUtils.getMoviePosterUrl(movieData.getPosterPath());
            Picasso.get().load(imageUrl).into(mMovieCoverImageView);
            mMovieDescriptionTextView.setText(movieData.getOverview());
        }
    }
}
