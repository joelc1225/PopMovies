package com.example.android.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.objects.Review;

import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{


    // used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {

        // member variables
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        // default constructor for ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);

            // find the views being displayed
            reviewAuthor = itemView.findViewById(R.id.author_name);
            reviewContent = itemView.findViewById(R.id.reviewContent);
        }
    }

    // member variable for list of reviews
    private final List<Review> mReviewsList;

    // constructor for adapter
    @SuppressWarnings("UnusedParameters")
    public ReviewAdapter(Context context, List<Review> reviews) {
        mReviewsList = reviews;
    }

    // below are the 3 primary recyclerView methods;
    // inflates the item layout and creates the holder
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate custom layout
        View review_view = inflater.inflate(R.layout.list_item_review, parent, false);

        //return a new holder instance
        return new ViewHolder(review_view);
    }

    // populates data into holder
    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        // get the data based on position
        Review current_review = mReviewsList.get(position);

        // set the data
        TextView authorView = holder.reviewAuthor;
        TextView reviewContent = holder.reviewContent;
        authorView.setText(current_review.mReviewAuthor);
        reviewContent.setText(current_review.mReviewContent);
    }

    @Override
    public int getItemCount() {
        return mReviewsList == null ? 0 : mReviewsList.size();
    }

    @SuppressWarnings("ConstantConditions")
    public void swap(List<Review> newReviewList) {

        if (mReviewsList == null || mReviewsList.size() == 0) {
            mReviewsList.addAll(newReviewList);
            notifyDataSetChanged();
        }
    }
}

