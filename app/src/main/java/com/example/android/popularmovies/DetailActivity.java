package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.Util;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.titleMain)TextView titleTV;
    @BindView(R.id.synopsis)TextView synopsisTV;
    @BindView(R.id.releaseDate)TextView releaseTV;
    @BindView(R.id.user_rating)TextView ratingTV;
    @BindView(R.id.poster)ImageView posterIV;
    @BindView(R.id.favorite)CheckBox favouriteBT;
    @BindView(R.id.reviewsTV)TextView reviewsTV;
    @BindView(R.id.linear_layout)LinearLayout mainLayout;
    int movieId;
    ArrayList<String> youtubeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatail);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        int position = mIntent.getIntExtra("position", 0);
        final Movie movie = mIntent.getExtras().getParcelable("movie");
        movieId = movie.getId();
        getYoutubeMovieId();
        getReviews();
        UpdateDetailView(movie);

        if(checkIfMovieIsFavourite() == true){
            favouriteBT.setChecked(true);
            Log.v("REVIEW", "MOVIE IS FAVOURITE");
        }

        favouriteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movieId != 0) {
                    if(favouriteBT.isChecked())
                        Utils.saveFavouriteMovie(DetailActivity.this, movie);
                    else {
                        //Utils.removeFavouriteMovie(DetailActivity.this, movie);
                        int id = movie.getId();
                        Utils.deleteTaskContentProvider(DetailActivity.this, "" + id);
                    }
                }
            }
        });
    }

    private void UpdateDetailView(Movie movie){
        if(movie != null) {
            titleTV.setText(movie.getTitle());
            synopsisTV.setText(movie.getSynopsis());
            releaseTV.setText(movie.getRelease());
            ratingTV.setText(movie.getRating());
            Picasso.with(this).load(Utils.getBasePicturePath() + movie.getThumbnail()).into(posterIV);
        }
    }
    //https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(Utils.getBaseYoutubePath() + id));
        Log.v("TEST TEST TEST", "URL" + Utils.getBaseYoutubePath() + id);
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public void getYoutubeMovieId(){
        new GetYoutubeMovieId().execute();
    }

    public class GetYoutubeMovieId extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response = Utils.getResponse(Utils.getYoutubeLink(movieId));
            Log.v("TEST TEST", "Response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                youtubeId = Utils.parseYoutubeVideoResponse(s);
                int i = 1;
                for (final String link:youtubeId
                     ) {
                    Button linkBT = new Button(DetailActivity.this);
                    linkBT.setText("TRAILER " + i);
                    linkBT.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    mainLayout.addView(linkBT);
                    linkBT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            watchYoutubeVideo(DetailActivity.this, link);
                        }
                    });
                    Log.v("REVIEW", "BUTTON ADDED");
                    i++;
                }
            }
        }
    }

    public void getReviews(){
        new GetMovieReviews().execute();
    }

    public class GetMovieReviews extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response = Utils.getResponse(Utils.getReviewsLink(movieId));
            Log.v("TEST TEST", "Response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                ArrayList<Review> reviews = Utils.getReviewsArrayFromJSON(s);
                if(reviews != null){
                    Log.v("REVIEW", "No of reviews " + reviews.size());
                    int i = 1;
                    for (Review r:reviews
                            ) {
                        TextView reviewTV = new TextView(DetailActivity.this);
                        reviewTV.setText("\nREVIEW " + i + "\n" + r.content);
                        reviewTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                        mainLayout.addView(reviewTV);
                        reviewsTV.setVisibility(View.GONE);
                        i++;
                    }
                } else {
                    reviewsTV.setText("No reviews");
                }
            }
        }
    }

    private boolean checkIfMovieIsFavourite(){
        if(movieId != 0){
            ArrayList<Movie> movies =  Utils.getFavouriteMovies(DetailActivity.this);
            for (Movie m: movies
                 ) {
                if(m.getId() == movieId)
                    return true;
            }
        }
        return false;
    }
}
