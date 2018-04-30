package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.android.popmovies.adapters.MovieAdapter;
import com.example.android.popmovies.data.MovieContract;
import com.example.android.popmovies.objects.Movie;
import com.example.android.popmovies.utilities.FetchUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MainActivityFragment extends AppCompatActivity {

    public static final String BASE_URL_MOVIES_POPULAR = "https://api.themoviedb.org/3/movie/popular?";

    public static final String BASE_URL_MOVIES_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated?";

    // TODO remember to remove API key before submitting project!!!! **************************
    public static final String API_KEY = "api_key=2499d4a4f8ad842fb5ed41d5c6542d79";

    // adapters used to populate the gridView
    private static MovieAdapter movieAdapter;

    private static ProgressBar progressBar;

    public static GridView gridView;

    private static List<Movie> movieList;

    Parcelable gridViewState;

    // Variable to determine if list is sorted already
    // 0 == most popular / 1 == top rated / 2 == Favorites
    public static int sortOrder =0;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_main);

        getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        movieAdapter = new MovieAdapter(getApplicationContext(), new ArrayList<Movie>());
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(movieAdapter);

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (savedInstanceState == null) {
            if (isConnected) {
                Log.d("IS CONNECTED", " ABOUT TO START MOVIE TASK");
                Log.d("URL = ",  BASE_URL_MOVIES_POPULAR + API_KEY);
                new MovieDatabaseTask().execute(BASE_URL_MOVIES_POPULAR + API_KEY);
            }
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Movie currentMovie = movieAdapter.getItem(position);

                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                i.putExtra("movie", currentMovie);
                startActivity(i);
            }
        });
    }

    static class MovieDatabaseTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... urls) {
            String searchUrlString = urls[0];
            URL searchUrl;
            String jsonSearchResults;
            movieList = null;

            try {
                searchUrl = new URL(searchUrlString);
                jsonSearchResults = FetchUtils.getResponseFromHttpUrl(searchUrl);
                movieList = FetchUtils.extractMoviesFromResponse(jsonSearchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {

            progressBar.setVisibility(View.GONE);

            // clear previous movie data, if any
            movieAdapter.clear();

            if (movieList != null && !movieList.isEmpty()) {
                movieAdapter.addAll(movieList);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }

    static class FavoriteDatabaseTask extends AsyncTask<Context, Void, Cursor> {


        @Override
        protected Cursor doInBackground(Context... contexts) {
            Context context = contexts[0];
            try {
                return context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            } catch (Exception e) {
                Log.e("ERROR ", "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {

            // empty arrayList that we'll fill by using the cursor
            List<Movie> movieListFromCursor = new ArrayList<>() ;

            int cursorSize = cursor.getCount();
            Log.d("CURSOR SIZE IS", " " + cursorSize);

            for (int i = 0; i < cursorSize; i++) {

                // move cursor to necessary movie in cursor
                cursor.moveToPosition(i);

                // find all the indices that we'll need to create a Movie to then add to an arrayList
                int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
                int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH);
                int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                int plotOverviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW);
                int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
                int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);

                // find all the movie attributes from the cursor
                String movieTitle = cursor.getString(titleIndex);
                String posterPath = cursor.getString(posterPathIndex);
                String releaseDate = cursor.getString(releaseDateIndex);
                String plotOverView = cursor.getString(plotOverviewIndex);
                double voteAverage = cursor.getDouble(voteAverageIndex);
                int movieId = cursor.getInt(movieIdIndex);

                movieListFromCursor.add(new Movie(movieTitle,
                        releaseDate,
                        posterPath,
                        voteAverage,
                        plotOverView,
                        movieId));
            }

            // clear previous movie data, if any
            movieAdapter.clear();

            if (movieListFromCursor != null && !movieListFromCursor.isEmpty()) {
                movieAdapter.addAll(movieListFromCursor);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item click here
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            // CALL DIALOG HERE using helper method
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        FragmentManager fm = getSupportFragmentManager();
        sortingFragment alertDialog = sortingFragment.newInstance(getString(R.string.sort_by), sortOrder);
        alertDialog.show(fm, "sorting options");
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onBackPressed() {
        // screen would previously go blank without this if 'back' was pressed in this fragment
        // i don't know why
        //noinspection UnnecessaryReturnStatement
        return;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        Log.d("SORT ORDER CODE IS ", "" + sortOrder);
        if (sortOrder == 2) {
            new FavoriteDatabaseTask().execute(getApplicationContext());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int index = gridView.getFirstVisiblePosition();
        gridViewState = gridView.onSaveInstanceState();
        super.onSaveInstanceState(outState);
        outState.putParcelable("GRID_VIEW_STATE", gridViewState);
        outState.putInt("SCROLL_INDEX", index);
        outState.putParcelableArrayList("MOVIE_ARRAY", (ArrayList<? extends Parcelable>) movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        gridViewState = savedInstanceState.getParcelable("GRID_VIEW_STATE");
        int index = savedInstanceState.getInt("SCROLL_INDEX");
        movieList = savedInstanceState.getParcelableArrayList("MOVIE_ARRAY");
        gridView.onRestoreInstanceState(gridViewState);
        movieAdapter.addAll(movieList);
        gridView.setSelection(index);
        progressBar.setVisibility(View.GONE);
    }
}

