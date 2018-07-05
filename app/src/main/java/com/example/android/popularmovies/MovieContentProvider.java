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
import android.util.Log;

import java.io.Console;

/**
 * Created by 1 on 12.05.2018.
 */

public class MovieContentProvider extends ContentProvider {
    private MovieDbHelper movieDbHelper;

    public static final int ENTRIES = 100;
    public static final int SINGLE_ENTRY = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("com.example.android.popularmovies", "entry", ENTRIES);
        uriMatcher.addURI("com.example.android.popularmovies", "entry/#", SINGLE_ENTRY);
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

        int match = uriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                long newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                return Uri.withAppendedPath(uri,String.valueOf(newRowId));
        }
        return null;

    }

    @Override
    public int delete (@NonNull Uri uri, String selection, String[]selectionArgs){
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int tasksDeleted;
        switch (match) {
            case SINGLE_ENTRY:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "movieId=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            switch (uriMatcher.match(uri)) {
                case ENTRIES:
                    if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                    break;
                case SINGLE_ENTRY:
                    selection = selection + "_ID = " + uri.getLastPathSegment();
                    break;

                default:
                    break;
            }
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return cursor;
    }
}
