package com.example.Tuner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    MenuItem item;
    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon,fastfBtn,slowdBtn,rely,plop,favma;
    ArrayList<AudioModel> songsList,lol;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    Button opla;
    int x=0;
    private boolean alreadyPlayedNextSong = false;
    private boolean checkFlag = false, repeatFlag = false, playContinueFlag = false, favFlag = true, playlistFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        favma=findViewById(R.id.favimgb);
        plop=findViewById(R.id.img_btn_setting);
        fastfBtn=findViewById(R.id.ffbt);
        slowdBtn=findViewById(R.id.ssb);
        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        opla=findViewById(R.id.open_playlist);
        titleTv.setSelected(true);
        rely=findViewById(R.id.img_btn_replay);



        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        // Retrieve the list of songs and the clicked position from the intent
        setResourcesWithMusic();


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                    if (favFlag) {
                        //Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                        favma.setImageResource(R.drawable.favorite_icon);
                    } else {
                        favma.setImageResource(R.drawable.ic_favorite_filled);
                    }

                }

                /*if (mediaPlayer != null) {

                }*/
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());
        fastfBtn.setOnClickListener(v-> fastforward());
        slowdBtn.setOnClickListener(v-> slowdown());
        opla.setOnClickListener(v-> workk());
        rely.setOnClickListener(v-> replaysongs());
        plop.setOnClickListener(v-> playloop());
        favma.setOnClickListener(v-> myfavsong());

        playMusic();


    }
    private void myfavsong(){

       // R.id.menu_favorites
        if (mediaPlayer != null) {
            if (favFlag) {
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
              //  item.setIcon(R.drawable.ic_favorite_filled);
                //Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                SongsList favList = new SongsList(currentSong.getTitle(),currentSong.getSubTitle(),currentSong.getPath());
                FavoritesOperations favoritesOperations = new FavoritesOperations(this);
                favoritesOperations.addSongFav(favList);
                favFlag = false;
            } else {
                //item.setIcon(R.drawable.favorite_icon);
                favFlag = true;
            }
        }

    }

    private void playloop(){
            if (!playContinueFlag) {
        playContinueFlag = true;
        Toast.makeText(this, "Loop Added", Toast.LENGTH_SHORT).show();
    } else {
        playContinueFlag = false;
        Toast.makeText(this, "Loop Removed", Toast.LENGTH_SHORT).show();
    }
}


    private void replaysongs(){
        if (repeatFlag) {
            Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show();
            mediaPlayer.setLooping(false);
            repeatFlag = false;
        } else {
            Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show();
            mediaPlayer.setLooping(true);
            repeatFlag = true;
        }
    }
    private void workk(){
        Intent intent = new Intent(MusicPlayerActivity.this, playlistviews.class);
        startActivity(intent);
    }

    private void fastforward(){

        if(mediaPlayer.isPlaying()){
            int currentPosition = mediaPlayer.getCurrentPosition();
            int fastForwardTime = 10000; // Fast forward by 10 seconds (adjust as needed)
            int newPosition = currentPosition + fastForwardTime;
            mediaPlayer.seekTo(newPosition);
        }

    }
    private void slowdown(){
        if(mediaPlayer.isPlaying()){
            int currentPosition = mediaPlayer.getCurrentPosition();
            int fastForwardTime = -10000; // go back by 10 seconds (adjust as needed)
            int newPosition = currentPosition + fastForwardTime;
            mediaPlayer.seekTo(newPosition);
        }
    }


    private void playMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void playNextSong(){

        if(MyMediaPlayer.currentIndex== songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }


    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}