package com.example.draw;

import android.media.MediaPlayer;

public class Song {
    private String name;
    private int pictureId;
    private int songId;

    public Song(String name, int pictureId, int songId) {
        this.name = name;
        this.pictureId = pictureId;
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
