package com.example.Tuner;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class FavoriteSongsActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private int currentPosition = -1;
    private ArrayList<SongsList> favoriteSongsList;
    private FavoritesOperations favoritesOperations;
    private FavoriteSongsAdapter adapter;
    private ListView listViewFavoriteSongs;
    private ImageView previousButton;
    private ImageView playPauseButton;
    private ImageView nextButton,refreshre;
    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);
        refreshre=findViewById(R.id.refresh);
        listViewFavoriteSongs = findViewById(R.id.list_view_favorite_songs);
        previousButton = findViewById(R.id.previous_button);
        playPauseButton = findViewById(R.id.play_pause_button);
        nextButton = findViewById(R.id.next_button);
        seekBar = findViewById(R.id.seek_bar);
        currentTimeTextView = findViewById(R.id.text_current_time);
        totalTimeTextView = findViewById(R.id.text_total_time);

        // Retrieve the list of favorite songs from the database using FavoritesOperations
        favoritesOperations = new FavoritesOperations(this);
        favoriteSongsList = favoritesOperations.getAllFavorites();

        // Create and set the adapter for the ListView
        adapter = new FavoriteSongsAdapter(this, favoriteSongsList);
        listViewFavoriteSongs.setAdapter(adapter);

        listViewFavoriteSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Play the selected song
                playSong(position);
            }
        });
        listViewFavoriteSongs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Delete the long-pressed song
                showDialog(favoriteSongsList.get(position).getPath(), position);
                return true;
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pauseSong();
                } else {
                    resumeSong();
                }
            }
        });

        refreshre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshView();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });

        // Initialize the handler for updating the seek bar and time
        handler = new Handler();
    }

    private void playSong(int position) {
        if (mediaPlayer != null && currentPosition == position) {
            // Song is already playing, do nothing
            return;
        }

        stopSong();

        // Create a new MediaPlayer instance
        mediaPlayer = new MediaPlayer();

        try {
            // Set the data source to the selected song's path
            String songPath = favoriteSongsList.get(position).getPath();
            mediaPlayer.setDataSource(songPath);

            // Prepare the MediaPlayer asynchronously
            mediaPlayer.prepareAsync();

            // Set the listener for when the MediaPlayer is prepared
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Start playing the song
                    mp.start();
                    currentPosition = position;
                    adapter.setCurrentlyPlayingPosition(position);
                    adapter.notifyDataSetChanged();
                    updateSeekBar();
                    updatePlayPauseButton();
                    updateCurrentTimeTextView();
                    updateTotalTimeTextView();
                }
            });

            // Set the listener for when the MediaPlayer has finished playing the song
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //stopSong();
                    int nextPosition = (currentPosition + 1) % favoriteSongsList.size();
                    playNextSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentPosition = -1;
        }
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
    private void showDialog(final String index, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_text))
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoritesOperations.removeSong(index);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updatePlayPauseButton();
        }
    }

    private void resumeSong() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updatePlayPauseButton();
        }
    }

    private void playPreviousSong() {
        int previousPosition = currentPosition - 1;
        if (previousPosition >= 0) {
            playSong(previousPosition);
        }
    }

    private void playNextSong() {
        int nextPosition = currentPosition + 1;
        if (nextPosition < favoriteSongsList.size()) {
            playSong(nextPosition);
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        updateCurrentTimeTextView();
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void updatePlayPauseButton() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }
    }

    private void updateCurrentTimeTextView() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentTime = formatTime(currentPosition);
            currentTimeTextView.setText(currentTime);
        }
    }

    private void updateTotalTimeTextView() {
        if (mediaPlayer != null) {
            int totalTime = mediaPlayer.getDuration();
            String totalTimeStr = formatTime(totalTime);
            totalTimeTextView.setText(totalTimeStr);
        }
    }

    private String formatTime(int timeInMillis) {
        int seconds = (timeInMillis / 1000) % 60;
        int minutes = (timeInMillis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void refreshView() {
        favoriteSongsList = favoritesOperations.getAllFavorites();
        adapter = new FavoriteSongsAdapter(this, favoriteSongsList);
        listViewFavoriteSongs.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSong();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSong();
    }

}