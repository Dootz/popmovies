//Based on https://developer.android.com/guide/topics/ui/layout/gridview.html
package com.example.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 10.03.2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies;

    public ImageAdapter(Context c, List<Movie> movies) {
        mContext = c; mMovies = movies;
    }

    public int getCount() {
        if(mMovies != null)
            return mMovies.size();
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if(mMovies != null){
            Picasso.with(mContext).load(Utils.getBasePicturePath()+mMovies.get(position).getThumbnail()).into(imageView);
            return imageView;
        }
        return null;
    }

    public void addItems(ArrayList<Movie> movieList){
        this.mMovies = movieList;
        notifyDataSetChanged();
    }
}