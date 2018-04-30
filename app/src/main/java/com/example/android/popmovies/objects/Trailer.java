package com.example.android.popmovies.objects;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popmovies.R;

public class Trailer implements Parcelable {

    // key is used to create the youtube url
    public final String mTrailer_key;
    public final String mTrailer_name;


    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };

    public Trailer(String key, String name) {
        this.mTrailer_key = key;
        this.mTrailer_name = name;
    }

    private Trailer(Parcel in) {
        mTrailer_key = in.readString();
        mTrailer_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTrailer_key);
        parcel.writeString(mTrailer_name);
    }

    // helper method to create Youtube link
    public String makeYoutubeLink(String key, Context context) {

        Resources resources = context.getResources();
        return resources.getString(R.string.base_youtube_link) + key;
    }
}
