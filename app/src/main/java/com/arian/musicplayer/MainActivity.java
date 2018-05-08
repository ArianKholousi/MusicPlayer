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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

//import com.yanzhenjie.permission.Action;
//import com.yanzhenjie.permission.AndPermission;
//import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ListFragment.Callbacks, PlayFragment.Callbacks, AddLyricsDialogFragment.Callbacks{


    private ViewPager viewPager;

    public static List<Song> songList;
    public static Uri currentSongPath;
    public static Song currentSong;
    public static int currentSongIndex;
    public static boolean isShuffle;
    public static boolean isRepeated;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;


    private MediaBrowserCompat.ConnectionCallback mediaBrowserConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();

            try {
                mediaController = new MediaControllerCompat(MainActivity.this, mediaBrowser.getSessionToken());
                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public List<Song> getSongList() {

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, sortOrder);

        List<Song>songs = new ArrayList<>();

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

                    songs.add(new Song(id,data,title,artist,album,albumID,duration));

                } while (musicCursor.moveToNext());
            }
        } finally {
            musicCursor.close();
        }

        return songs;
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

        songList = getSongList();

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
    protected void onDestroy() {
        super.onDestroy();
        mediaBrowser.disconnect();
    }

    @Override
    public void initPlayFragment() {
        PlayFragment playFragment = (PlayFragment) viewPager.getAdapter().instantiateItem(viewPager,1);
        playFragment.init();
    }

    @Override
    public List<Song> getSongs() {
        List<Song> songs = getSongList();
        return songs;
    }

    @Override
    public void updateList() {
        ListFragment listFragment = (ListFragment) viewPager.getAdapter().instantiateItem(viewPager,0);
        listFragment.updateRecyclerView();
    }

    @Override
    public void showLyricsFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container_lyrics,LyricsFragment.newInstance()).commit();
    }

    @Override
    public void showAddLyricDialogFragment() {
        AddLyricsDialogFragment.newInstance().show(getSupportFragmentManager(),"add_lyric_dialog_tag");
    }

    @Override
    public void showAddLyricsFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container_lyrics,AddLyricsFragment.newInstance()).commit();
    }
}
