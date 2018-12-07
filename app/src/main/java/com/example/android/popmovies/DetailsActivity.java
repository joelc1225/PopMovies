package com.example.android.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.adapters.ReviewAdapter;
import com.example.android.popmovies.adapters.TrailerAdapter;
import com.example.android.popmovies.data.MovieContract;
import com.example.android.popmovies.objects.Movie;
import com.example.android.popmovies.objects.Review;
import com.example.android.popmovies.objects.Trailer;
import com.example.android.popmovies.utilities.FetchUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TRAILERS_URL_PATH = "/videos?";
    private static final String REVIEWS_URL_PATH = "/reviews?";
    private static final String API_KEY = MainActivityFragment.API_KEY;
    private static TrailerAdapter trailerAdapter;
    private static ReviewAdapter reviewAdapter;
    private ArrayList<Trailer> mTrailerArrayList;
    private ImageButton favoriteButton;
    private int movie_id;
    private int favorite_id;
    private static final String BASE_SHARED_PREF_STRING_KEY = "Movie_id=";
    private String favoriteUriString;

    // ** FORMAT FOR URL: BASE_URL + movie_id + URL_PATH + API_KEY **

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        // get intent and movie data from extra
        Intent incomingIntent = getIntent();
        final Movie selectedMovie = incomingIntent.getParcelableExtra("movie");

        // sets movie id used to fetch the trailer and review info from API
        movie_id = selectedMovie.mMovie_id;

        // finds favorite button before we check shared prefs
        favoriteButton = findViewById(R.id.favoriteButton);

        // does some checks to prep the layout with for correct star color
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains(BASE_SHARED_PREF_STRING_KEY + movie_id)) {
            // changes the color of the star to show if movie is currently a favorite
            DrawableCompat.setTint(
                    favoriteButton.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorStarYellow));

            // change favorite id to 1 so that the favorite button operates correctly
            favorite_id = 1;

            // prepares the uri for the current movie in case user removes from faves
           favoriteUriString = sharedPreferences.getString(
                   BASE_SHARED_PREF_STRING_KEY + movie_id, null);
        }

        // Removes up button, because it was behaving abnormally
        getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon

        // Find all views from details activity
        ImageView posterImg = (ImageView) findViewById(R.id.posterIv);
        TextView title = (TextView) findViewById(R.id.title_Tv);
        TextView releaseDate = (TextView) findViewById(R.id.releaseDateTv);
        TextView voterAvg = (TextView) findViewById(R.id.voterAvgTv);
        TextView plotSummary = (TextView) findViewById(R.id.plotSummaryTv);
        RecyclerView rvTrailers = (RecyclerView) findViewById(R.id.trailersRecyclerView);
        RecyclerView rvReviews = (RecyclerView) findViewById(R.id.reviewsRecyclerView);


        // instantiate member variables for arrays
        mTrailerArrayList = new ArrayList<>();
        ArrayList<Review> mReviewArrayList = new ArrayList<>();

        Context context = getApplicationContext();

        // initialize adapters
        trailerAdapter = new TrailerAdapter(context, mTrailerArrayList, this);
        reviewAdapter = new ReviewAdapter(context, mReviewArrayList);
        rvReviews.setAdapter(reviewAdapter);
        rvTrailers.setAdapter(trailerAdapter);
        rvTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvReviews.setLayoutManager(new LinearLayoutManager(this));



        // creates string urls to fetch trailer/review data from API
        String trailers_url_string = API_BASE_URL + movie_id + TRAILERS_URL_PATH + API_KEY;
        String reviews_url_string = API_BASE_URL + movie_id + REVIEWS_URL_PATH + API_KEY;

        // call trailerTask and reviewTask to fetch data from API and display to activity
        new TrailerTask().execute(trailers_url_string);
        new ReviewTask().execute(reviews_url_string);

        // create text to display for release date and vote avg
        String releaseDateString =
                getString(R.string.release_date) + " " + selectedMovie.mReleaseDate;
        String voteAvgString =
                getString(R.string.vote_avg) + " " + String.valueOf(selectedMovie.mVoteAverage);

        // Set the data to the views
        Picasso.get().load(selectedMovie.mImagePath).into(posterImg);
        title.setText(selectedMovie.mTitle);
        releaseDate.setText(releaseDateString);
        voterAvg.setText(voteAvgString);
        plotSummary.setText(selectedMovie.mPlotOverview);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (favorite_id == 0) {

                    // changes the color of the star to show if movie is currently a favorite
                    DrawableCompat.setTint(
                            favoriteButton.getDrawable(),
                            ContextCompat.getColor(getApplicationContext(),
                                    R.color.colorStarYellow));

                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_faves), Toast.LENGTH_SHORT).show();

                    favorite_id = 1;

                    new insertTask().execute(selectedMovie);

                } else {

                    new AlertDialog.Builder(DetailsActivity.this)
                            .setTitle("Removal Confirmation")
                            .setMessage("Remove from Favorites?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.remove_alert_button, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // changes favorite id back to 0 to correct the star color
                                    // when activity restarts
                                    favorite_id = 0;

                                    Log.d("CHECKING PREFS FOR KEY", BASE_SHARED_PREF_STRING_KEY + movie_id);
                                    // removes the key in shared prefs so that when we check to see
                                    // if the movie is a favorite, it wont be there
                                    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove(BASE_SHARED_PREF_STRING_KEY + movie_id);
                                    editor.apply();

                                    Log.d("FAVE URI TO DELETE", favoriteUriString);
                                    new deleteTask().execute(Uri.parse(favoriteUriString));
                                    Toast.makeText(getApplicationContext(), getString(R.string.removed_from_faves), Toast.LENGTH_SHORT).show();

                                    DrawableCompat.setTint(
                                            favoriteButton.getDrawable(),
                                            ContextCompat.getColor(getApplicationContext(),
                                                    R.color.colorStarWhite));
                                }})
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
            }
        });
    }

    // launches intent to youtube video for trailers
    @Override
    public void onListItemClick(int clickedItemPosition) {

        // gets trailer in clicked position, extracts the youtube key, creates youtube url string
        Trailer currentTrailer = mTrailerArrayList.get(clickedItemPosition);
        String youtubeTrailerKey = currentTrailer.mTrailer_key;
        String youtubeUrlString = currentTrailer.makeYoutubeLink(youtubeTrailerKey, getApplicationContext());
        Uri youtubeUri = Uri.parse(youtubeUrlString);

        // create intent to launce youtube link and launch, if possible
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
        if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(youtubeIntent);
        }


    }

    private static class TrailerTask extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... urls) {
            String searchUrlString = urls[0];
            URL searchUrl;
            String jsonSearchResults;
            List<Trailer> trailerList = null;

            try {
                searchUrl = new URL(searchUrlString);
                jsonSearchResults = FetchUtils.getResponseFromHttpUrl(searchUrl);
                trailerList = FetchUtils.extractTrailersFromResponse(jsonSearchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trailerList;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerList) {
            trailerAdapter.swap(trailerList);
        }
    }

    private static class ReviewTask extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... urls) {
            String searchUrlString = urls[0];
            URL searchUrl;
            String jsonSearchResults;
            List<Review> reviewList = null;

            try {
                searchUrl = new URL(searchUrlString);
                jsonSearchResults = FetchUtils.getResponseFromHttpUrl(searchUrl);
                reviewList = FetchUtils.extractReviewsFromResponse(jsonSearchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewList;
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
            reviewAdapter.swap(reviewList);
        }
    }

    // helper method that creates content values from Movie parameters
    private ContentValues createContentValues(Movie movie) {

        ContentValues values = new ContentValues();
        values.put("title", movie.mTitle);
        values.put("releaseDate", movie.mReleaseDate);
        values.put("imagePath", movie.mImagePath);
        values.put("voteAverage", movie.mVoteAverage);
        values.put("plotOverview", movie.mPlotOverview);
        values.put("movieId", movie.mMovie_id);

        return values;
    }

    private class insertTask extends AsyncTask<Movie, Void, Uri> {

        @Override
        protected Uri doInBackground(Movie... movies) {
            Log.d("DO IN BACKGROUND ", "STARTED!!!");
            Movie selectedMovie = movies[0];
            final ContentValues values = createContentValues(selectedMovie);

            return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            // movie was successfully added to favorites database and we can add
            // the movie id to shared prefs
            favoriteUriString = uri.toString();
            Log.d("FAVE URI STRING: ", favoriteUriString);
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("ADDING", BASE_SHARED_PREF_STRING_KEY + movie_id );
            editor.putString(BASE_SHARED_PREF_STRING_KEY + movie_id, favoriteUriString);
            editor.apply();
        }
    }

    private class deleteTask extends AsyncTask<Uri, Void, Integer> {

        @Override
        protected Integer doInBackground(Uri... uris) {
            Uri movieUri = uris[0];
            Log.d("URI TO DELETE IS: ", movieUri.toString());

            return getContentResolver().delete(movieUri, null, null);
        }
    }
}
