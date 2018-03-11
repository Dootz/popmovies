package com.example.android.popularmovies;

import android.database.Cursor;
import android.nfc.Tag;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
                String id = movieJSON.getString(JSON_MOVIE_ID);
                Movie movie = new Movie(title, posterPath, description, voteAvg, releaseDate, id);
                movieList.add(movie);
            }
        } catch (JSONException ex){
            Log.v(TAG, "JSON parse exception");
        }
        return movieList;
    }

    public static String getBasePicturePath(){
        return baseImgUrl+imgSize;
    }

    public static String getYoutubeLink(String id){
        return baseUrl + id + movieVideos + apiKey;
    }

    public static String getBaseYoutubePath() {
        return baseYoutubePath;
    }

    public static String parseYoutubeVideoResponse(String response){
        try {
            JSONObject root = new JSONObject(response);
            JSONArray movies = root.getJSONArray(JSON_RESULTS);
            String key = movies.getJSONObject(0).getString(JSON_KEY);
            return key;

        } catch(JSONException ex){
            Log.v(TAG, "JSON parse exception at Youtube Video Link parsing");
        }
        return null;
    }
}
