package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.BaseColumns;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 1 on 09.03.2018.
 */

public class Utils {
    private static String baseUrl = "http://api.themoviedb.org/3/movie/";
    private static String movieRated = "top_rated";
    private static String moviePopular = "popular";
    private static String TAG = "Utils";
    private static String baseImgUrl = "http://image.tmdb.org/t/p/";
    private static String imgSize = "w185";
    private static String apiKey = "?api_key=YOUR API KEY HERE";
    private static String JSON_RESULTS = "results";
    private static String JSON_AVG = "vote_average";
    private static String JSON_TITLE = "title";
    private static String JSON_DESC = "overview";
    private static String JSON_RELEASE = "release_date";
    private static String JSON_POSTER = "poster_path";
    private static String JSON_MOVIE_ID = "id";
    private static String JSON_KEY = "key";
    private static String movieVideos = "/videos";
    private static String movieReviews = "/reviews";
    private static String baseYoutubePath = "https://youtube.com/watch?v=";

    public static String assembleRequestURL(boolean popularMovies){
        StringBuilder url = new StringBuilder(baseUrl);
        if(popularMovies)
            url.append(moviePopular);
        else
            url.append(movieRated);
        url.append(apiKey);

        return url.toString();
    }

    public static String getResponse(String url) {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            Log.v("TEST TEST ", "TEST TEST " + url);
            return response.body().string();
        } catch (IOException ex) {
            Log.v(TAG, "IOException on reading response");
        }
        return null;
    }

    public static ArrayList<Movie> parseMoviesArrayFromJSON(String json){
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray movies = root.getJSONArray(JSON_RESULTS);
            for(int i = 0; i < movies.length(); i++) {
                JSONObject movieJSON = movies.getJSONObject(i);
                String title = movieJSON.getString(JSON_TITLE);
                String voteAvg = movieJSON.getString(JSON_AVG);
                String description = movieJSON.getString(JSON_DESC);
                String releaseDate = movieJSON.getString(JSON_RELEASE);
                String posterPath = movieJSON.getString(JSON_POSTER);
                int id = movieJSON.getInt(JSON_MOVIE_ID);
                Movie movie = new Movie(title, posterPath, description, voteAvg, releaseDate, id);
                movieList.add(movie);
            }
        } catch (JSONException ex){
            Log.v(TAG, "JSON parse exception");
        }
        return movieList;
    }

    public static ArrayList<Review> getReviewsArrayFromJSON(String json){
        ArrayList<Review> reviewsList = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray reviews = root.getJSONArray(JSON_RESULTS);
            if(reviews.length() == 0)
                return null;
            for(int i = 0; i < reviews.length(); i++) {
                JSONObject reviewsJSON = reviews.getJSONObject(i);
                String author = reviewsJSON.getString("author");
                String content = reviewsJSON.getString("content");
                reviewsList.add(new Review(content, author));
            }
        } catch (JSONException ex){
            Log.v(TAG, "JSON parse exception");
        }

        return reviewsList;
    }

    public static String getBasePicturePath(){
        return baseImgUrl+imgSize;
    }

    public static String getYoutubeLink(int id){
        return baseUrl + id + movieVideos + apiKey;
    }

    public static String getReviewsLink(int id) { return baseUrl + id + movieReviews + apiKey;}

    public static String getBaseYoutubePath() {
        return baseYoutubePath;
    }

    public static ArrayList<String> parseYoutubeVideoResponse(String response){
        ArrayList<String> youtubeLinks = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(response);
            JSONArray movies = root.getJSONArray(JSON_RESULTS);
            Log.v("REVIEW", "Length " + movies.length());
            for (int i = 0; i < movies.length(); i++) {
                youtubeLinks.add(movies.getJSONObject(i).getString(JSON_KEY));
                Log.v("REVIEW", "Item added " + movies.getJSONObject(i).getString(JSON_KEY));
            }
            return youtubeLinks;

        } catch(JSONException ex){
            Log.v(TAG, "JSON parse exception at Youtube Video Link parsing");
        }
        return null;
    }
    public static void saveFavouriteMovie(Context mContext, Movie movie){
        MovieDbHelper mDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_THUMBNAIL, movie.getThumbnail());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_RATING, movie.getRating());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE, movie.getRelease());
        values.put(MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS, movie.getSynopsis());
        Log.v("REVIEW", "SAVING MOVIE ID = " + movie.getId());
        //long newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

        Uri uri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

    public static void removeFavouriteMovie(Context mContext, Movie movie){
        MovieDbHelper mDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_NAME_ID + " =" + movie.getId(), null);
    }

    public static ArrayList<Movie> getFavouriteMovies(Context mContext){
        MovieDbHelper mDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                MovieContract.MovieEntry.COLUMN_NAME_TITLE,
                MovieContract.MovieEntry.COLUMN_NAME_THUMBNAIL,
                MovieContract.MovieEntry.COLUMN_NAME_ID,
                MovieContract.MovieEntry.COLUMN_NAME_RATING,
                MovieContract.MovieEntry.COLUMN_NAME_RELEASE,
                MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS
        };

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<Movie> movies = new ArrayList<Movie>();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RATING));
            String thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_THUMBNAIL));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE));
            String synopsis = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_TITLE));
            movies.add(new Movie(title,thumbnail,synopsis,rating,release,itemId));
            Log.v("REVIEW", "READ item id " + itemId);
        }
        cursor.close();
        return movies;
    }

    public static void deleteTaskContentProvider(Context mContext, String stringId) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        // COMPLETED (2) Delete a single row of data using a ContentResolver
        mContext.getContentResolver().delete(uri, null, null);
    }

    public static ArrayList<Movie> getFavouriteMoviesContentProvider(Context mContext){

        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RATING));
            String thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_THUMBNAIL));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE));
            String synopsis = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_TITLE));
            movies.add(new Movie(title,thumbnail,synopsis,rating,release,itemId));
            Log.v("REVIEW", "READ item id " + itemId);
        }
        cursor.close();
        return movies;
    }

}
