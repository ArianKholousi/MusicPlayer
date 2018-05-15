package com.arian.musicplayer;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.arian.musicplayer.MainActivity.STATE_PAUSED;
import static com.arian.musicplayer.MainActivity.STATE_PLAYING;
import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MainActivity.currentSongIndex;
import static com.arian.musicplayer.MainActivity.currentSongPath;
import static com.arian.musicplayer.MainActivity.songList;


/**
 * Created by Payami on 04/25/2018.
 */

public class MediaPlaybackService extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFICATION_ID = 0;
    public static MediaPlayer mediaPlayer;
    public static MediaSessionCompat mediaSession;

    public static boolean isShuffle;
    public static boolean isRepeated;

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {


        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }

            try {
                mediaPlayer.setDataSource(mediaId);
            } catch (IOException e) {
                Log.d("mytag9", "error" + mediaId);
            }

            mediaSession.setActive(true);

            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showNotification(STATE_PLAYING);

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        }

        @Override
        public void onPlay() {
            super.onPlay();
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showNotification(STATE_PLAYING);
            mediaPlayer.start();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showNotification(STATE_PAUSED);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mediaSession.setActive(false);
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

    };


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mediaPlayer == null)
            initMediaPlayer();

        initMediaSession();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeated)
            mediaPlayer.start();

        if (isShuffle)
            mediaSessionCallback.onSkipToNext();

    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "tag_media_session", mediaButtonReceiver, null);
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        setSessionToken(mediaSession.getSessionToken());
    }


    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackstateBuilder.build());
    }


    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = 0;
        if (audioManager != null) {
            result = audioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) initMediaPlayer();
                else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer != null)
                    mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private void showNotification(int state) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getArtist())
                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()))
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (state == STATE_PLAYING) {
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)));
        } else if (state == STATE_PAUSED) {
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)));
        }

        NotificationManagerCompat.from(MediaPlaybackService.this).notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.abandonAudioFocus(this);
        }

        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }

}
