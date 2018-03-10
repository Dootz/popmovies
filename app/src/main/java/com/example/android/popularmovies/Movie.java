package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 1 on 09.03.2018.
 */

public class Movie implements Parcelable {
    private String title;
    private String thumbnail;
    private String synopsis;
    private String rating;
    private String release;

    public Movie(String title, String thumbnail, String synopsis, String rating, String release) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.synopsis = synopsis;
        this.rating = rating;
        this.release = release;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.synopsis);
        dest.writeString(this.release);
        dest.writeString(this.thumbnail);
        dest.writeString(this.rating);
    }
    // Parcelling part
    public Movie(Parcel in){
        // the order needs to be the same as in writeToParcel() method
        this.title = in.readString();
        this.synopsis = in.readString();
        this.release = in.readString();
        this.thumbnail = in.readString();
        this.rating = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
