package com.arian.musicplayer;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;


import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.arian.musicplayer.MainActivity.STATE_PAUSED;
import static com.arian.musicplayer.MainActivity.STATE_PLAYING;
import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MainActivity.currentSongIndex;
import static com.arian.musicplayer.MainActivity.currentState;
import static com.arian.musicplayer.MainActivity.songList;
import static com.arian.musicplayer.MediaPlaybackService.isRepeated;
import static com.arian.musicplayer.MediaPlaybackService.isShuffle;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public SeekBar seekBar;
    public ImageButton btnPlayPause;

    private ImageView songImage;
    private TextView tvPassedSeconds;
    private TextView tvSongDuration;
    private TextView tvSongTitle;
    private TextView tvSongArtist;
    private TextView tvSongAlbum;
    private ImageButton btnShuffle;
    private ImageButton btnRepeat;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnLyrics;

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
                tvPassedSeconds.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
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
        callbacks = null;
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
        btnLyrics = (ImageButton) view.findViewById(R.id.image_button_lyrics);

        seekBar.setOnSeekBarChangeListener(this);
        btnPlayPause.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnLyrics.setOnClickListener(this);

        init();

        return view;
    }

    public void init() {
        if (currentSong != null) {
            seekBar.setMax((int) (currentSong.getDuration() / 1000));
            handler.removeCallbacks(updateSeekbar);
            handler.postDelayed(updateSeekbar, 0);

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, currentSong.getAlbumID());
//            Picasso.with(getActivity()).load(albumArtUri).placeholder(R.drawable.icon_music_480).noFade().fit().into(songImage);
            Glide.with(getActivity()).load(albumArtUri).apply(new RequestOptions().placeholder(R.drawable.icon_music_480)).into(songImage);

            tvSongDuration.setText(formatDuration(currentSong.getDuration()));
            tvSongTitle.setText(currentSong.getTitle());
            tvSongArtist.setText(currentSong.getArtist());
            tvSongAlbum.setText(currentSong.getAlbum() == null ? "Music" : currentSong.getAlbum());
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.image_button_lyrics:
                if (LyricsPreferences.getStoredList(getActivity(), String.valueOf(currentSong.getId())) == null) {
                    callbacks.showAddLyricDialogFragment();
                } else
                    callbacks.showLyricsFragment();
                break;


            case R.id.image_play:
                if (currentState == STATE_PAUSED) {
//                    btnPlayPause.setBackgroundResource(R.drawable.icon_pause_64);
                    btnPlayPause.setImageResource(R.drawable.icon_pause_64);
                    getActivity().getSupportMediaController().getTransportControls().play();
                    currentState = STATE_PLAYING;
                } else if (currentState == STATE_PLAYING) {
                    btnPlayPause.setImageResource(R.drawable.icon_play_64);
//                    btnPlayPause.setBackgroundResource(R.drawable.icon_play_64);
                    getActivity().getSupportMediaController().getTransportControls().pause();
                    currentState = STATE_PAUSED;
                }
                break;


            case R.id.image_repeat:
                if (isRepeated) {
                    isRepeated = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off);
//                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                } else {
                    isRepeated = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
//                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_on);
                }
                break;


            case R.id.image_shuffle:
                if (isShuffle) {
                    isShuffle = false;
                    Collections.sort(songList);
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
//                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                } else {
                    isShuffle = true;
                    Collections.shuffle(songList);
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_on);

//                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                }
                callbacks.updateList();
                break;


            case R.id.image_next:

                if (isShuffle) {
                    currentSongIndex++;
                    if (currentSongIndex == songList.size()) {
                        currentSongIndex = 0;
                    }
                    currentSong = songList.get(currentSongIndex);
                    init();
                }
                getActivity().getSupportMediaController().getTransportControls().stop();
                getActivity().getSupportMediaController().getTransportControls().playFromMediaId(currentSong.getData(), null);
                break;


            case R.id.image_previous:

                if (mediaPlayer.getCurrentPosition() / 1000 < 10) {
                    if (isShuffle) {
                        currentSongIndex--;
                        if (currentSongIndex < 0)
                            currentSongIndex = songList.size() - 1;

                        currentSong = songList.get(currentSongIndex);
//                        currentSongPath = Uri.parse(currentSong.getData());
                        init();
                    }
                }
                getActivity().getSupportMediaController().getTransportControls().stop();
                getActivity().getSupportMediaController().getTransportControls().playFromMediaId(currentSong.getData(), null);
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

    public interface Callbacks {
        void updateList();
        void showLyricsFragment();
        void showAddLyricDialogFragment();
        void setBtnPlayDrawable();
        void setBtnPauseDrawable();
    }


}
