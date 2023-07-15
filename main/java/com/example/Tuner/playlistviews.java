package com.example.Tuner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class playlistviews extends AppCompatActivity {

    private Button addPlaylistButton;
    private ListView playlistListView;
    private ArrayAdapter<Playlist> playlistAdapter;
    private List<Playlist> playlists;
    private DatabaseHelper databaseHelper;
    ArrayList<AudioModel> playlistdata = new ArrayList<>();
    RecyclerView recyclerView;
    TextView noMusicTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlistviews);
        databaseHelper = new DatabaseHelper(this);
        addPlaylistButton = findViewById(R.id.add_playlist_button);
        recyclerView = findViewById(R.id.playkav);
        noMusicTextView = findViewById(R.id.no_songs_text);
        playlistListView = findViewById(R.id.recyc);
        playlists = new ArrayList<>();
        playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlists);
        playlistListView.setAdapter(playlistAdapter);
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylist();
            }
        });


        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist selectedPlaylist = playlists.get(position);
                // Handle playlist selection, e.g., display songs in the playlist
              //  List<Song> songs = databaseHelper.getSongsByPlaylist(selectedPlaylist);
                // Update your UI accordingly
             //   ArrayAdapter<Song> songAdapter = new ArrayAdapter<>(parent.getContext(), android.R.layout.simple_list_item_1, songs);
              // ListView songListView = findViewById(R.id.song_list_view);
                //songListView.setAdapter(songAdapter);
                displaySongsInPlaylist(selectedPlaylist);
            }
        });
        registerForContextMenu(playlistListView);
        loadPlaylists();
    }

    private void addPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(playlistviews.this);
        builder.setTitle("Add Playlist");
        final EditText playlistNameEditText = new EditText(playlistviews.this);
        playlistNameEditText.setHint("Enter playlist name");
        builder.setView(playlistNameEditText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistName = playlistNameEditText.getText().toString();
                if (!TextUtils.isEmpty(playlistName.trim())) {
                    Playlist playlist = new Playlist(playlistName);
                    DatabaseHelper databaseHelper = new DatabaseHelper(playlistviews.this);
                    databaseHelper.addPlaylist(playlist);
                    Toast.makeText(playlistviews.this, "Playlist Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(playlistviews.this, "Please enter playlist name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void loadPlaylists() {
        playlists.clear();
        playlists.addAll(databaseHelper.getAllPlaylists());
        playlistAdapter.notifyDataSetChanged();
        playlists = databaseHelper.getAllPlaylists();
        playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlists);
        playlistListView.setAdapter(playlistAdapter);
    }

    private void displaySongsInPlaylist(final Playlist playlist) {
        //**/*/*/*/*/*/
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                playlistdata.add(songData);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        //*/*/*/*///**//*/**///
       // final List<Song> allSongs = databaseHelper.getAllSongs();

        AlertDialog.Builder builder = new AlertDialog.Builder(playlistviews.this);
        builder.setTitle("Add Song to Playlist");

        // Create a list of song titles for the dialog
        List<String> songTitles = new ArrayList<>();
        for (AudioModel song : playlistdata) {
            songTitles.add(song.getTitle());
        }

        final CharSequence[] songTitlesArray = songTitles.toArray(new CharSequence[0]);


        // Set up the dialog with a single-choice list
       builder.setSingleChoiceItems(songTitlesArray, 0, new DialogInterface.OnClickListener() {
        //builder.setSingleChoiceItems(opp, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected song
                        if (!playlistdata.isEmpty()) {
                            AudioModel selectedSong = playlistdata.get(which);
                            // Pass the selected song data to the OtherClass activity


                            // Add the selected song to the playlist
                            //databaseHelper.addSongToPlaylist(selectedSong, playlist);
                            Toast.makeText(playlistviews.this, "Song added to playlist", Toast.LENGTH_SHORT).show();

                            // Refresh the song list in the selected playlist view
                           // updatePlaylistSongList(playlist);


                        } else {
                            // Handle the case when allSongs is empty
                            Toast.makeText(playlistviews.this, "No songs available", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


   private void updatePlaylistSongList(Playlist playlist) {
       List<Song> songs = databaseHelper.getSongsByPlaylist(playlist);
       ArrayAdapter<Song> songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);

       View songListViewLayout = getLayoutInflater().inflate(R.layout.activity_playlist_songs, null);

       // Inflate the layout that contains the song_list_view
       ListView songListView = songListViewLayout.findViewById(R.id.song_list_view);
       if (songListView != null) {
           // Set the adapter for the ListView
           songListView.setAdapter(songAdapter);

           // Set the inflated layout as the content view of a new dialog
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Playlist Songs");
           builder.setView(songListViewLayout);
           builder.setPositiveButton("OK", null);
           builder.show();
       } else {
           String TAG="lol";
           Log.e(TAG, "ListView song_list_view not found in the layout");
       }
   }







    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.playlist_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Playlist selectedPlaylist = playlists.get(info.position);
        switch (item.getItemId()) {
            case R.id.edit_playlist:
                editPlaylist(selectedPlaylist);
                return true;
            case R.id.delete_playlist:
                deletePlaylist(selectedPlaylist);
                return true;
            case R.id.open_playlist:
                openPlaylist(selectedPlaylist);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void openPlaylist(Playlist playlist) {
        // Example: Start a new activity to display the playlist
        Intent intent = new Intent(this, playlistkaview.class);
        intent.putExtra("playlist_id", playlist.getId());
        startActivity(intent);
    }

    private void editPlaylist(final Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(playlistviews.this);
        builder.setTitle("Edit Playlist");
        final EditText playlistNameEditText = new EditText(playlistviews.this);
        playlistNameEditText.setHint("Enter playlist name");
        playlistNameEditText.setText(playlist.getName());
        builder.setView(playlistNameEditText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistName = playlistNameEditText.getText().toString();
                if (!TextUtils.isEmpty(playlistName)) {
                    playlist.setName(playlistName);
                    databaseHelper.updatePlaylist(playlist);
                    loadPlaylists();
                    Toast.makeText(playlistviews.this, "Playlist updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(playlistviews.this, "Please enter playlist name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deletePlaylist(final Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(playlistviews.this);
        builder.setTitle("Delete Playlist");
        builder.setMessage("Are you sure you want to delete this playlist?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.deletePlaylist(playlist);
                loadPlaylists();
                Toast.makeText(playlistviews.this, "Playlist deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}