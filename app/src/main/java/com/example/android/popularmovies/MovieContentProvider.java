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
        long newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

        return Uri.withAppendedPath(uri,String.valueOf(newRowId));
    }

    @Override
    public int delete (@NonNull Uri uri, String selection, String[]selectionArgs){

        // Get access to the database and write URI matching code to recognize a single item
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Log.v("REVIEW", "Content uri " + uri.toString());
        int match = uriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case SINGLE_ENTRY:
                // Get the task ID from the URI path
                Log.v("REVIEW", "Uri matched");
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID

                tasksDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "movieId=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        Log.v("REVIEW", "DELETED TASK " + tasksDeleted);
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
