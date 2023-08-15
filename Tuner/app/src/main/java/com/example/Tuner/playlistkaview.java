package com.example.Tuner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class playlistkaview extends AppCompatActivity {
ListView songskalist;
    private DatabaseHelper databaseHelper;
    private ListView playlistListView;
    private ArrayAdapter<Song> playlistAdapter;
    List<Song> ssongTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlistkaview);
        songskalist.findViewById(R.id.playkav);
        ssongTitles = new ArrayList<>();
        ArrayAdapter<Song> playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ssongTitles);
        playlistListView.setAdapter(playlistAdapter);
        registerForContextMenu(playlistListView);
        loadPlaylists();
    }


    private void loadPlaylists() {
        ssongTitles.clear();
        ssongTitles.addAll(databaseHelper.getSongs());
        playlistAdapter.notifyDataSetChanged();
    }


}

