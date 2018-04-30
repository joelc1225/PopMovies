package com.example.android.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.objects.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // member variables for views
        private final TextView title_view;

        private ViewHolder(View itemView) {
            super(itemView);

            title_view = itemView.findViewById(R.id.trailerName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    // member variables for adapter
    private final List<Trailer> mTrailerList;

    @SuppressWarnings("UnusedParameters")
    public TrailerAdapter(Context context, List<Trailer> trailerList, ListItemClickListener listener) {
        mTrailerList = trailerList;
        mOnClickListener = listener;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflates custom layout
        View trailerView = inflater.inflate(R.layout.list_item_trailer, parent, false);

        // returns the holder instance
        return new ViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, int position) {
        // get data from item in current position
        Trailer currentTrailer = mTrailerList.get(position);

        TextView titleView = holder.title_view;
        titleView.setText(currentTrailer.mTrailer_name);
    }

    @Override
    public int getItemCount() {
        return mTrailerList == null ? 0 : mTrailerList.size();
    }

    @SuppressWarnings("ConstantConditions")
    public void swap(List<Trailer> newTrailerList) {

        if (mTrailerList == null || mTrailerList.size() == 0) {
            mTrailerList.addAll(newTrailerList);
            notifyDataSetChanged();
        }
    }
}
