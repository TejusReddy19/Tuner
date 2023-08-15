package com.example.Tuner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class FavoriteSongsAdapter extends ArrayAdapter<SongsList> {
    private int currentlyPlayingPosition = -1;
    private Context context;
    private ArrayList<SongsList> songsList;

    public FavoriteSongsAdapter(Context context, ArrayList<SongsList> songsList) {
        super(context, 0, songsList);
        this.context = context;
        this.songsList = songsList;
    }
    public void setCurrentlyPlayingPosition(int position) {
        currentlyPlayingPosition = position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        SongsList song = getItem(position);

        TextView songTitleTextView = convertView.findViewById(android.R.id.text1);
        songTitleTextView.setText(song.getTitle());
        if (position == currentlyPlayingPosition) {
            // Set the highlight color for the currently playing song
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700));
        } else {
            // Reset the background color for other songs
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }


        return convertView;
    }
}
