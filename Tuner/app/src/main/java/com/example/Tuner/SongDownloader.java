package com.example.Tuner;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SongDownloader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_downloader);

    }
    private static final String SEARCH_API_URL = "https://example.com/search?query=";
    private static final String DOWNLOAD_API_URL = "b376f42b98msh5c7e702770aae4ap1b0b61jsn3dd2e1a8076a";
    private static final String SONG_TITLE = "Song.mp3";

    private Context context;

    public SongDownloader(Context context) {
        this.context = context;
    }

    public void searchAndDownloadSong(String searchTerm) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            performSearch(searchTerm);
        } else {
            Toast.makeText(context, "Storage permission required.", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch(String searchTerm) {
        // Construct the search URL by appending the search term
        String searchUrl = SEARCH_API_URL + searchTerm;

        // Perform a network request to search for the song
        new SearchTask().execute(searchUrl);
    }

    private void downloadSong(String songDownloadUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songDownloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(SONG_TITLE)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, SONG_TITLE);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(context, "DownloadManager not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (urls.length > 0) {
                String searchUrl = urls[0];
                try {
                    URL url = new URL(searchUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        return response.toString();
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse the result to extract the download URL
                // ...
                // Implement your parsing logic here to extract the download URL from the result

                // For demonstration purposes, let's assume the download URL is obtained successfully
                String songDownloadUrl = DOWNLOAD_API_URL + result;

                if (songDownloadUrl != null) {
                    downloadSong(songDownloadUrl);
                } else {
                    Toast.makeText(context, "Song not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Failed to perform search.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}