package com.example.android.popmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("CanBeFinal")
public class Movie implements Parcelable {

    public String mTitle;
    public String mReleaseDate;
    public String mImagePath; // string to append call to api to retrieve image
    public double mVoteAverage; // may need to format later to only show 2 decimal places. idk yet
    public String mPlotOverview;
    public int mMovie_id;

    public Movie(String title, String releaseDate, String imagePath, double voteAverage, String plotOverview, int movie_id) {
        this.mTitle = title;
        this.mReleaseDate = releaseDate;
        this.mImagePath = imagePath;
        this.mVoteAverage = voteAverage;
        this.mPlotOverview = plotOverview;
        this.mMovie_id = movie_id;
    }

    private Movie(Parcel in) {
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mImagePath = in.readString();
        mVoteAverage = in.readDouble();
        mPlotOverview = in.readString();
        mMovie_id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mImagePath);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mPlotOverview);
        parcel.writeInt(mMovie_id);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
