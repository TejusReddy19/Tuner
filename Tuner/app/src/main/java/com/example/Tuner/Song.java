package com.example.Tuner;

public class Song {
    private int id;
    private String name;
    //private String artist;
    // Other song properties

    public Song(String name) {
        this.name = name;
        //this.artist = artist;
        // Initialize other song properties
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    //public String getArtist() {return artist;}

}
