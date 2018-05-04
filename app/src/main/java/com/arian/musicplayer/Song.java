package com.arian.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Payami on 04/24/2018.
 */

public class Song  implements Parcelable, Comparable<Song> {
    private long id;
    private String data;
    private String title;
    private String artist;
    private String album;
    private long albumID;
    private long duration;


    public Song(long id, String data, String title, String artist, String album, long albumID, long duration) {
        this.id = id;
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumID = albumID;
        this.duration = duration;
    }

    protected Song(Parcel in) {
        id = in.readLong();
        data = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        albumID = in.readLong();
        duration = in.readLong();
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

    public long getAlbumID() {
        return albumID;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(data);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeLong(albumID);
        dest.writeLong(duration);
    }


    @Override
    public int compareTo(@NonNull Song o) {
        return this.getTitle().compareTo(o.getTitle());
    }
}
