package com.example.Tuner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noMusicTextView;
     DrawerLayout mDrawerLayout;
     NavigationView navigationView;
     Toolbar toolbar;
    private Menu menu;
    private String searchText = "";
    ArrayList<AudioModel> songsList = new ArrayList<>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            init();
            recyclerView = findViewById(R.id.recycler_view);
            noMusicTextView = findViewById(R.id.no_songs_text);
            if(checkPermission() == false){
                requestPermission();
                return;
            }
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
                    songsList.add(songData);
            }
            if(songsList.size()==0){
                noMusicTextView.setVisibility(View.VISIBLE);
            }else{
                //recyclerview
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
            }
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.addAllSongsToDatabase(this);
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        }
    private void init() {
            try {
        mDrawerLayout=findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);
                toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (item.getItemId()) {
                            case R.id.nav_about:
                                about();
                                return true;
                        }
                        return false;
                    }
                });
           }catch(Exception e){
            Log.d("op","nop");
        }
    }
        boolean checkPermission(){
            int result = ContextCompat.checkSelfPermission(com.example.Tuner.MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(result == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                return false;
            }
        }
    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optional: Add code to handle button click if needed
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
        void requestPermission(){
            if(ActivityCompat.shouldShowRequestPermissionRationale(com.example.Tuner.MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(com.example.Tuner.MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTTINGS",Toast.LENGTH_SHORT).show();
            }else
                ActivityCompat.requestPermissions(com.example.Tuner.MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
        }
        @Override
        protected void onResume() {
            super.onResume();
            if(recyclerView!=null){
                recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
            }

        }
        //
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    return true;
                case R.id.menu_search:
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_favorites:
                    openFavoriteSongsList();
                    return true;
                case R.id.aboutpaa:
                    about();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

    private void openFavoriteSongsList() {
        Intent intent = new Intent(this, FavoriteSongsActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                searchSongs();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public String getSearchText() {
        return searchText.toLowerCase();
    }
    private void searchSongs() {
        String searchQuery = getSearchText();
        ArrayList<AudioModel> filteredList = new ArrayList<>();

        for (AudioModel song : songsList) {
            if (song.getTitle().toLowerCase().contains(searchQuery)) {
                filteredList.add(song);
            }
        }
        if (filteredList.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        } else {
            noMusicTextView.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new MusicListAdapter(filteredList, getApplicationContext()));
    }
}