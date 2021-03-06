package com.arian.musicplayer;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.arian.musicplayer.MainActivity.STATE_PLAYING;
import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MainActivity.currentSongIndex;
import static com.arian.musicplayer.MainActivity.currentState;



/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String ARGS_SONGS_LIST = "args_songs_list";
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private Callbacks callbacks;


    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(List<Song> songs) {

        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        args.putParcelableArrayList(ARGS_SONGS_LIST, (ArrayList<? extends Parcelable>) songs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songList = getArguments().getParcelableArrayList(ARGS_SONGS_LIST);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        List<Song> songs = callbacks.getSongs();
        final List<Song> filteredModelList = new ArrayList<>();
        for (Song model : songs) {
            final String textTitle = model.getTitle().toLowerCase();
            final String textArtist = model.getArtist().toLowerCase();
            if (textTitle.contains(newText) || textArtist.contains(newText))
                filteredModelList.add(model);
        }
        songAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvArtist;
        private Song song;

        public SongHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_song_list);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_song_title_list);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_song_singer_list);
            itemView.setOnClickListener(this);
        }

        public void bindSong(Song song) {
            this.song = song;
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, song.getAlbumID());
//            Picasso.with(getActivity()).load(albumArtUri).placeholder(R.drawable.icon_music_96).noFade().fit().into(imageView);
            Glide.with(getActivity()).load(albumArtUri).apply(new RequestOptions().placeholder(R.drawable.icon_music_96)).into(imageView);

        }

        @Override
        public void onClick(View v) {
            if (currentState == STATE_PLAYING)
                getActivity().getSupportMediaController().getTransportControls().stop();

            currentSong = song;
            currentSongIndex = songList.indexOf(currentSong);
            getActivity().getSupportMediaController().getTransportControls().playFromMediaId(currentSong.getData(), null);

            if (getFragmentManager().findFragmentById(R.id.container_lyrics) != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container_lyrics)).commit();
            }
            callbacks.initPlayFragment();
        }
    }


    private class SongAdapter extends RecyclerView.Adapter<SongHolder> {

        private List<Song> songs;

        public SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }

        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_list_item, parent, false);
            return new SongHolder(view);
        }

        @Override
        public void onBindViewHolder(SongHolder holder, int position) {
            Song song = songs.get(position);
            holder.bindSong(song);
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }


        public Song removeItem(int position) {
            final Song model = songs.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, Song model) {
            songs.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Song model = songs.remove(fromPosition);
            songs.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<Song> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }


        private void applyAndAnimateRemovals(List<Song> newModels) {
            for (int i = songs.size() - 1; i >= 0; i--) {
                final Song model = songs.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Song> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final Song model = newModels.get(i);
                if (!songs.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Song> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final Song model = newModels.get(toPosition);
                final int fromPosition = songs.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }

    public void updateRecyclerView() {
        if (songAdapter == null) {
            songAdapter = new SongAdapter(songList);
            recyclerView.setAdapter(songAdapter);
        } else {
            songAdapter.notifyDataSetChanged();
        }
    }


    public interface Callbacks {
        void initPlayFragment();
        List<Song> getSongs();
    }


}
