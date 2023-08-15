package com.example.Tuner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    private LayoutInflater inflater;

    public SongAdapter(Context context, List<Song> songs) {
        super(context, 0, songs);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Song song = getItem(position);
        TextView textView = itemView.findViewById(android.R.id.text1);
        textView.setText(song.getName());

        return itemView;
    }
}
