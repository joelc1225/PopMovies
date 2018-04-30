package com.example.android.popmovies;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static com.example.android.popmovies.MainActivityFragment.API_KEY;
import static com.example.android.popmovies.MainActivityFragment.BASE_URL_MOVIES_POPULAR;
import static com.example.android.popmovies.MainActivityFragment.BASE_URL_MOVIES_TOP_RATED;


public class sortingFragment extends DialogFragment {


    public sortingFragment() {
        // Required empty public constructor
    }

    private static int sortOrderCode;

    public static sortingFragment newInstance(String title, int sortOrderCode) {
        sortingFragment frag = new sortingFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("sortOrderCode", sortOrderCode);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        sortOrderCode = getArguments().getInt("sortOrderCode");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setItems(R.array.sorting_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                switch (position) {

                    case 0:
                        if (sortOrderCode == 0) {
                            Toast.makeText(getContext(), "Already sorted by most popular", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivityFragment.sortOrder = 0;
                            MainActivityFragment.gridView.setSelection(0);
                            new MainActivityFragment.MovieDatabaseTask().execute(BASE_URL_MOVIES_POPULAR + API_KEY);
                        }
                        break;

                    case 1:
                        if (sortOrderCode == 1) {
                            Toast.makeText(getContext(), "Already sorted by avg voter rating", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivityFragment.sortOrder = 1;
                            MainActivityFragment.gridView.setSelection(0);
                            new MainActivityFragment.MovieDatabaseTask().execute(BASE_URL_MOVIES_TOP_RATED + API_KEY);
                        }
                        break;

                    case 2:
                        if (sortOrderCode == 2) {
                            Toast.makeText(getContext(), "Already sorted by favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivityFragment.sortOrder = 2;
                            MainActivityFragment.gridView.setSelection(0);
                            new MainActivityFragment.FavoriteDatabaseTask().execute(getActivity().getApplicationContext());

                        }
                }
            }
        });
        return alertDialogBuilder.create();
    }

}
