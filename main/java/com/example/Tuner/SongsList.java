package com.example.Tuner;

public class SongsList {

    private String title;
    private String subTitle;
    private String path;
    String duration;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SongsList(String title, String subTitle, String path) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
        this.duration = duration;


    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getDuration() {return duration;
    }
}
