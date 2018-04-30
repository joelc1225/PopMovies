package com.example.android.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    // Empty constructor
    private MovieContract() {
    }

    final static String CONTENT_AUTHORITY = "com.example.android.popmovies";
    private final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // table name
    final static String PATH_MOVIES = "movies";

    // inner class defines constants for the movies database table
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String TABLE_NAME = "movies";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_IMAGE_PATH = "imagePath";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_PLOT_OVERVIEW = "plotOverview";
        public static final String COLUMN_MOVIE_ID = "movieId";
    }
}
