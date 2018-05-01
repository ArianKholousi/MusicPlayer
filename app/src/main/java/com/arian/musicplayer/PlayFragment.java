package com.arian.musicplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import static com.arian.musicplayer.MainActivity.STATE_PAUSED;
import static com.arian.musicplayer.MainActivity.STATE_PLAYING;
import static com.arian.musicplayer.MainActivity.currentState;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {

    ImageButton imageBtnPause;



    public PlayFragment() {
        // Required empty public constructor
    }

    public static PlayFragment newInstance() {

        Bundle args = new Bundle();

        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        imageBtnPause = (ImageButton) view.findViewById(R.id.image_play);
        imageBtnPause.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        if( currentState == STATE_PAUSED ) {
            MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
            currentState = STATE_PLAYING;
        } else {
            if( MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
               MediaControllerCompat.getMediaController(getActivity()).getTransportControls().pause();
            }

            currentState = STATE_PAUSED;
        }
    }

//    public void buildTransportControls() {
//        imagePlayPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int state = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getState();
//                if (state == PlaybackStateCompat.STATE_PLAYING) {
//                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().pause();
//                }else {
//
//                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
//                }
//            }
//        });
//
//        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
//
//        MediaMetadataCompat metadata = mediaController.getMetadata();
//        PlaybackStateCompat playbackState = mediaController.getPlaybackState();
//
//        mediaController.registerCallback(controllerCallback);
//    }


}
