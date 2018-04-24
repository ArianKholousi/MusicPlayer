package com.arian.musicplayer;

/**
 * Created by Payami on 04/24/2018.
 */

public class Song {
    private String data;
    private String title;
    private String artist;
    private String album;

    public Song(String data, String title, String artist, String album) {
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    public String getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }
}
