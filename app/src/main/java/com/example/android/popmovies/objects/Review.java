package com.example.android.popmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    public final String mReviewAuthor;
    public final String mReviewContent;
    private final String mReviewUrl;

    private Review(Parcel in) {
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
        mReviewUrl = in.readString();

    }

    public Review (String author, String content, String url) {
        this.mReviewUrl = url;
        this.mReviewContent = content;
        this.mReviewAuthor = author;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mReviewAuthor);
        parcel.writeString(mReviewContent);
        parcel.writeString(mReviewUrl);
    }
}
