package com.arian.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Payami on 04/24/2018.
 */

public class Song implements Parcelable{
    private long id;
    private String title;
    private String artist;
    private String album;

    public Song(long id, String title, String artist, String album) {
        this.id=  id;
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    protected Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public long getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
    }
}
