package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by 1 on 12.05.2018.
 */

public class MovieContentProvider extends ContentProvider {
    private MovieDbHelper movieDbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("com.example.android.popularmovies", "entry", 1);
        uriMatcher.addURI("com.example.android.popularmovies", "entry/#", 2);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        return db.update(MovieContract.MovieEntry.TABLE_NAME,  contentValues, "WHERE _ID =" + s, null);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        long newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

        return Uri.withAppendedPath(uri,String.valueOf(newRowId));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        return db.delete(MovieContract.MovieEntry.TABLE_NAME,"_ID" + "=" + s, null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            switch (uriMatcher.match(uri)) {
                case 1:
                    if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                    break;
                case 2:
                    selection = selection + "_ID = " + uri.getLastPathSegment();
                    break;

                default:
                    break;
            }
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        return cursor;
    }
}
