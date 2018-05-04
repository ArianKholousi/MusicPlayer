package com.arian.musicplayer;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MainActivity.currentSongIndex;
import static com.arian.musicplayer.MainActivity.currentSongPath;
import static com.arian.musicplayer.MainActivity.isShuffle;
import static com.arian.musicplayer.MainActivity.songList;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public SeekBar seekBar;
    private ImageView songImage;
    private TextView tvPassedSeconds;
    private TextView tvSongDuration;
    private TextView tvSongTitle;
    private TextView tvSongArtist;
    private TextView tvSongAlbum;
    private ImageButton btnShuffle;
    private ImageButton btnRepeat;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;

    private static Handler handler;
    private static Runnable updateSeekbar;
    private Callbacks callbacks;


    public static PlayFragment newInstance() {
        Bundle args = new Bundle();
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                seekBar.setProgress(currentPosition);
                handler.postDelayed(this, 1000);
            }
        };


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            callbacks = (Callbacks) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks =null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        songImage = (ImageView) view.findViewById(R.id.image_song_play);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        tvPassedSeconds = (TextView) view.findViewById(R.id.tv_passed);
        tvSongDuration = (TextView) view.findViewById(R.id.tv_duration);
        tvSongTitle = (TextView) view.findViewById(R.id.tv_song_title_play);
        tvSongArtist = (TextView) view.findViewById(R.id.tv_song_singer_play);
        tvSongAlbum = (TextView) view.findViewById(R.id.tv_song_album_play);
        btnShuffle = (ImageButton) view.findViewById(R.id.image_shuffle);
        btnRepeat = (ImageButton) view.findViewById(R.id.image_repeat);
        btnPrevious = (ImageButton) view.findViewById(R.id.image_previous);
        btnNext = (ImageButton) view.findViewById(R.id.image_next);
        btnPlayPause = (ImageButton) view.findViewById(R.id.image_play);

        seekBar.setOnSeekBarChangeListener(this);
        btnPlayPause.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

        init();

        return view;
    }

    public void init() {
        if (currentSong != null) {
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, currentSong.getAlbumID());
            Picasso.with(getActivity()).load(albumArtUri).placeholder(R.drawable.icon_music_480).noFade().fit().into(songImage);


            seekBar.setMax((int) (currentSong.getDuration() / 1000));
//            updateSeekbar.run();
            handler.postDelayed(updateSeekbar, 0);

            tvSongDuration.setText(formatDuration(currentSong.getDuration()));
            tvSongTitle.setText(currentSong.getTitle());
            tvSongArtist.setText(currentSong.getArtist());
            tvSongAlbum.setText(currentSong.getAlbum() == null ? "Music" : currentSong.getAlbum());
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_play:

                if (mediaPlayer.isPlaying()) {
                    btnPlayPause.setBackgroundResource(R.drawable.icon_play_64);
                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().pause();
                } else {
                    btnPlayPause.setBackgroundResource(R.drawable.icon_pause_64);
                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
                }
                break;

            case R.id.image_repeat:

                if (mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                }else {
                    mediaPlayer.setLooping(true);
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_on);
                }
                break;

            case R.id.image_shuffle:
                if (isShuffle){
                    isShuffle=false;
                    Collections.sort(songList);
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                }else {
                    isShuffle=true;
                    Collections.shuffle(songList);
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                }
                callbacks.updateList();
                break;


            case R.id.image_next:
                mediaPlayer.stop();
                mediaPlayer.reset();

                if (isShuffle){
                    if (currentSongIndex == songList.size()-1)
                        currentSongIndex = 0;
                    else
                        currentSongIndex++;
                    currentSong = songList.get(currentSongIndex);
                    currentSongPath = Uri.parse(songList.get(currentSongIndex).getData());
                    init();
                }
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromUri(currentSongPath, null);
                break;

            case R.id.image_previous:
                mediaPlayer.stop();
                mediaPlayer.reset();
                if (mediaPlayer.getCurrentPosition()/1000 < 10) {
                    if (isShuffle) {
                        if (currentSongIndex==0)
                            currentSongIndex = songList.size()-1;
                        else
                            currentSongIndex--;
                        currentSong = songList.get(currentSongIndex);
                        currentSongPath = Uri.parse(songList.get(currentSongIndex).getData());
                        init();
                    }
                }
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromUri(currentSongPath, null);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mediaPlayer != null && fromUser)
            mediaPlayer.seekTo(progress * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public static String formatDuration(long duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
    }

    public interface Callbacks{
        void updateList();
    }


}
