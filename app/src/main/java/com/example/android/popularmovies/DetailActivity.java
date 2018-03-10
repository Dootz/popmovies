package com.example.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.titleMain)TextView titleTV;
    @BindView(R.id.synopsis)TextView synopsisTV;
    @BindView(R.id.releaseDate)TextView releaseTV;
    @BindView(R.id.user_rating)TextView ratingTV;
    @BindView(R.id.poster)ImageView posterIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatail);
        ButterKnife.bind(this);
        Movie movie = null;
        Intent mIntent = getIntent();
        int position = mIntent.getIntExtra("position", 0);
        movie = mIntent.getExtras().getParcelable("movie");
        UpdateDetailView(movie);
    }

    private void UpdateDetailView(Movie movie){
        if(movie != null) {
            titleTV.setText(movie.getTitle());
            synopsisTV.setText(movie.getSynopsis());
            releaseTV.setText(movie.getRelease());
            ratingTV.setText(movie.getRating().toString());
            Picasso.with(this).load(Utils.getBasePicturePath() + movie.getThumbnail()).into(posterIV);
        }
    }
}
