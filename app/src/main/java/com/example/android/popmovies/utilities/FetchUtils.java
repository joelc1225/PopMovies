package com.example.android.popmovies.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.objects.Movie;
import com.example.android.popmovies.objects.Review;
import com.example.android.popmovies.objects.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class FetchUtils {

    // tag for logs
    private static final String LOG_TAG = "LOG MSG: ";

    // private constructor
    private FetchUtils() {
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("//A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }
    }

    public static List<Movie> extractMoviesFromResponse(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            Log.e(LOG_TAG, "JSON string is empty or null");
            return null;
        }

        // create an empty arrayList of Movie objects. we'll add to the array later.
        List<Movie> movies = new ArrayList<>();

        // base image url
        final String BASE_IMAGE_URL_STRING = "https://image.tmdb.org/t/p/w185";

        // Try to parse JSON response. if there's a problem, a JSON exception will be thrown.
        // Catch the exception so the app doesn't crash, and print to the logs.
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray moviesArray = jsonObject.getJSONArray("results");
            int arraySize = moviesArray.length();

            for (int i = 0; i < arraySize; i++) {
                JSONObject currentMovie = moviesArray.getJSONObject(i);
                String title = currentMovie.getString("title");
                String releaseDate = currentMovie.getString("release_date");
                int movie_id = currentMovie.getInt("id");
                String poster_path = BASE_IMAGE_URL_STRING + currentMovie.getString("poster_path");
                double vote_average = currentMovie.getDouble("vote_average");
                String plot_overview = currentMovie.getString("overview");

                movies.add(new Movie(title, releaseDate, poster_path, vote_average, plot_overview, movie_id));
            }
        } catch (JSONException e) {
            Log.e("FetchUtils", "Problem parsing the movie JSON results", e);
        }
        // returns the list of movies
        return movies;
    }

    public static List<Trailer> extractTrailersFromResponse(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            Log.e(LOG_TAG, "JSON string is empty or null");
            return null;
        }

        // create an empty arrayList of Trailer objects. we'll add to the array later.
        List<Trailer> trailers = new ArrayList<>();

        // try to parse the json response for trailers
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray trailersArray = jsonObject.getJSONArray("results");
            int arraySize = trailersArray.length();


            for (int i = 0; i < arraySize; i++) {
                JSONObject currentTrailer = trailersArray.getJSONObject(i);
                String trailerTitle = currentTrailer.getString("name");
                String trailerKey = currentTrailer.getString("key");

                trailers.add(new Trailer(trailerKey, trailerTitle));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Length trailers Array: ", String.valueOf(trailers.size()));
        return trailers;
    }

    public static List<Review> extractReviewsFromResponse(String jsonResponse) {

        List<Review> reviews = new ArrayList<>();

        // try to parse the json response for reviews
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray reviewsArray = jsonObject.getJSONArray("results");
            int arraySize = reviewsArray.length();


            for (int i = 0; i < arraySize; i++) {
                JSONObject currentReview = reviewsArray.getJSONObject(i);
                String reviewAuthor = currentReview.getString("author");
                String reviewContent = currentReview.getString("content");
                String reviewUrl = currentReview.getString("url");

                reviews.add(new Review(reviewAuthor, reviewContent, reviewUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Length reviews Array: ", String.valueOf(reviews.size()));
        return reviews;
    }

}
