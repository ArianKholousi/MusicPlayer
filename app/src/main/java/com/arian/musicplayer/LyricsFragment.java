package com.arian.musicplayer;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFragment extends Fragment {

    private TextView tvLyric;
    private SparseArray<String>lyrics;
    private Handler handler ;
    private Runnable runnable;


    public static LyricsFragment newInstance() {
        Bundle args = new Bundle();
        LyricsFragment fragment = new LyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LyricsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        lyrics = LyricsPreferences.getStoredList(getActivity(), String.valueOf(currentSong.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        tvLyric = (TextView) view.findViewById(R.id.tv_showing_lyric);
        init();

        return view;
    }

    public void init() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int i = mediaPlayer.getCurrentPosition()/1000;
                if (lyrics.get(i) != null)
                    tvLyric.setText(lyrics.get(i));

                handler.postDelayed(this,500);
            }
        };
        handler.postDelayed(runnable,0);
    }
}
