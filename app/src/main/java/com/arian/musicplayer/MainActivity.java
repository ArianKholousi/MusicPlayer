package com.arian.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

//import com.yanzhenjie.permission.Action;
//import com.yanzhenjie.permission.AndPermission;
//import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int STATE_PAUSED = 0;
    public static final int STATE_PLAYING = 1;
    public static int currentState;

    private ViewPager viewPager;
    private List<Song> songList;

    public static Uri currentSongPath;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;


    private MediaBrowserCompat.ConnectionCallback mediaBrowserConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();

            try {
                mediaController = new MediaControllerCompat(MainActivity.this, mediaBrowser.getSessionToken());
                mediaController.registerCallback(mediaControllerCallback);
                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
//                if(currentSongPath != null)
//                    MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().playFromUri(currentSongPath,null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if (state == null) {
                return;
            }
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    currentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    currentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };


    public void getSongList() {

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, sortOrder);

        songList = new ArrayList<>();

        if (musicCursor == null) {
            Toast.makeText(this, "No musics on the device", Toast.LENGTH_SHORT).show();
        } else if (!musicCursor.moveToFirst()) {
            Toast.makeText(this, "No musics on the device", Toast.LENGTH_SHORT).show();
        } else try {
            {
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                do {
                    long id = musicCursor.getLong(idColumn);
                    String data = musicCursor.getString(dataColumn);
                    String title = musicCursor.getString(titleColumn);
                    String artist = musicCursor.getString(artistColumn);
                    String album = musicCursor.getString(albumColumn);
                    long albumID = musicCursor.getLong(albumIDColumn);
                    long duration = musicCursor.getLong(durationColumn);

                    songList.add(new Song(id,data,title,artist,album,albumID,duration));

                } while (musicCursor.moveToNext());
            }
        } finally {
            musicCursor.close();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        AndPermission.with(this).permission(Permission.READ_EXTERNAL_STORAGE)
//                .onGranted(new Action() {
//                    @Override
//                    public void onAction(List<String> permissions) {
//                        getSongList();
//                        Log.d("mytag", String.valueOf(songList.size()));
//                    }
//                }).onDenied(new Action() {
//            @Override
//            public void onAction(List<String> permissions) {
//
//            }
//        }).start();

        getSongList();
        Log.d("mytag", String.valueOf(songList.size()));

        mediaBrowser = new MediaBrowserCompat(MainActivity.this, new ComponentName(MainActivity.this, MediaPlaybackService.class), mediaBrowserConnectionCallback, getIntent().getExtras());
        mediaBrowser.connect();

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return ListFragment.newInstance(songList);
                if (position == 1)
                    return PlayFragment.newInstance();

                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(MainActivity.this) != null) {
            MediaControllerCompat.getMediaController(MainActivity.this).unregisterCallback(mediaControllerCallback);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((MediaControllerCompat.getMediaController(this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)) {
            MediaControllerCompat.getMediaController(this).getTransportControls().pause();
        }
        mediaBrowser.disconnect();
    }
}
