package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Movie> movieList;
    private boolean popularMovies;
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieList = null;
        popularMovies = false;
        final Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        GridView gridview = (GridView)findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
               intent.putExtra("position", position);
               startActivity(intent);
            }
        });
        Update();
    }

    public class GetMovieInfo extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response = Utils.getResponse(Utils.assembleRequestURL(popularMovies));
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            int j = 0;
            if ( s != null){
                movieList = Utils.parseMoviesArrayFromJSON(s);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    public void Update(){
        if (isOnline()) {
            new GetMovieInfo().execute();
        } else {
            CharSequence text = "Check Your Internet Connection";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }
// based on https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        switch(itemid){
            case R.id.action_popular:
                popularMovies = true;
                break;
            case R.id.action_rated:
                popularMovies = false;
                break;
        }
        Update();
        return true;
    }

}
