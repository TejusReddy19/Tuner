package com.example.Tuner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music_player.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_SONG_ID = "song_id";
    private static final String COLUMN_SONG_NAME = "song_name";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";
    private static final String COLUMN_PLAYLIST_ID = "playlist_id";
    private static final String COLUMN_SONG_FILEPATH = "song_filepath";
    private static final String TAG = "MyTag";
    //private static final String COLUMN_FILE_PATH = "filepath";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPlaylistTableQuery = "CREATE TABLE " + TABLE_PLAYLISTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT)";
        db.execSQL(createPlaylistTableQuery);
        String createSongsTableQuery = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SONG_NAME + " TEXT,"+ COLUMN_SONG_FILEPATH + " TEXT)";
        db.execSQL(createSongsTableQuery);
        String createPlaylistSongsTableQuery = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PLAYLIST_ID + " INTEGER,"
                + COLUMN_SONG_NAME + " TEXT,"
                + " FOREIGN KEY (" + COLUMN_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + COLUMN_ID + "),"
                + " FOREIGN KEY (" + COLUMN_SONG_NAME + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_NAME + "))";
        db.execSQL(createPlaylistSongsTableQuery);


    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    public void addPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, playlist.getName());
        long playlistId =db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
        playlist.setId((int) playlistId);
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PLAYLISTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int playlistId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String playlistName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                Playlist playlist = new Playlist(playlistName);
                playlist.setId(playlistId);
                playlists.add(playlist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playlists;
    }

    public void updatePlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, playlist.getName());
        db.update(TABLE_PLAYLISTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(playlist.getId())});
        db.close();
    }

    public void deletePlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLISTS, COLUMN_ID + " = ?", new String[]{String.valueOf(playlist.getId())});
        db.delete(TABLE_PLAYLIST_SONGS, COLUMN_PLAYLIST_ID + " = ?", new String[]{String.valueOf(playlist.getId())});
        db.close();
    }


    /*public List<Song> getSongsByPlaylist(Playlist playlist) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SONGS + " INNER JOIN " + TABLE_PLAYLIST_SONGS +
                " ON " + TABLE_SONGS + "." + COLUMN_ID + " = " + TABLE_PLAYLIST_SONGS + "." + COLUMN_ID +
                " WHERE " + TABLE_PLAYLIST_SONGS + "." + COLUMN_PLAYLIST_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playlist.getId())});
        if (cursor.moveToFirst()) {
            do {
                int songId = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
                String songName = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME));
                Song song = new Song(songName);
                song.setId(songId);
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }*/

    public List<Song> getSongsByPlaylist(Playlist playlist) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_ID, COLUMN_SONG_NAME};

        String query = "SELECT " + TextUtils.join(",", projection) +
                " FROM " + TABLE_SONGS +
                " INNER JOIN " + TABLE_PLAYLIST_SONGS +
                " ON " + TABLE_SONGS + "." + COLUMN_ID + " = " + TABLE_PLAYLIST_SONGS + "." + COLUMN_SONG_ID +
                " WHERE " + TABLE_PLAYLIST_SONGS + "." + COLUMN_PLAYLIST_ID + " = ?";

        String[] selectionArgs = {String.valueOf(playlist.getId())};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                int songId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String songName = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME));
                Song song = new Song(songName);
                song.setId(songId);
                songs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return songs;
    }

    public void addSongToPlaylist(Song song, Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYLIST_ID, playlist.getId());
        values.put(COLUMN_SONG_NAME, song.getName());
        long id = db.insert(TABLE_PLAYLIST_SONGS, null, values);
        song.setId((int) id);

        db.close();
    }

    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PLAYLIST_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int songId = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
                String songName = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME));
                Song song = new Song(songName);
                song.setId(songId);
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }



    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SONGS ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                int songId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String songName = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME));
                Song song = new Song(songName);
                song.setId(songId);
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public void addsongs(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, playlist.getName());
        long playlistId =db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
        playlist.setId((int) playlistId);
    }

    public void addSongToDatabase(String songName, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_NAME, songName);
        values.put(COLUMN_SONG_FILEPATH, filePath);
        db.insert(TABLE_SONGS, null, values);
        db.close();
    }

    public void addAllSongsToDatabase(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String songName = cursor.getString(titleColumn);
                String filePath = cursor.getString(pathColumn);
                addSongToDatabase(songName, filePath);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

}



