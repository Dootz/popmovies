package com.example.android.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 1 on 11.05.2018.
 */

public class MovieContract {
    private MovieContract() {}

    public static final String AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ENTRY = "entry";

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRY).build();

        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_THUMBNAIL= "thumbnail";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_ID= "movieId";
        public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
        public static final String COLUMN_NAME_RELEASE = "release";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_NAME_TITLE + " TEXT," +
                    MovieEntry.COLUMN_NAME_THUMBNAIL + " TEXT," +
                    MovieEntry.COLUMN_NAME_SYNOPSIS + " TEXT," +
                    MovieEntry.COLUMN_NAME_RATING + " TEXT," +
                    MovieEntry.COLUMN_NAME_ID + " INTEGER," +
                    MovieEntry.COLUMN_NAME_RELEASE + " TEXT);";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;
}
