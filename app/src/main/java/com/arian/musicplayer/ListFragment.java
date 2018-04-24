package com.arian.musicplayer;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;


    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {

        Bundle args = new Bundle();

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songList = new ArrayList<>();
        getSongList();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateRecyclerView();


        return view;
    }


    private class SongHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvSinger;
        private Song song;

        public SongHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_song_list);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_song_title_list);
            tvSinger = (TextView) itemView.findViewById(R.id.tv_song_singer_list);

        }

        public void bindSong(Song song){
            this.song = song;


        }



    }


    private class SongAdapter extends RecyclerView.Adapter<SongHolder>{

        private List<Song> songs;

        public SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }

        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item,parent,false);
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
    }


    public void updateRecyclerView(){

        if (songAdapter == null){
            recyclerView.setAdapter(new SongAdapter(songList));
        }else {
            songAdapter.setSongs(songList);
            songAdapter.notifyDataSetChanged();
        }

    }


    public void getSongList(){

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri,null,selection,null,sortOrder);


        if (musicCursor != null && musicCursor.getCount() >0){

            try {
                while (musicCursor.moveToNext()){
                    String data = getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String album = getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    songList.add(new Song(data,title,artist,album));
                }
            } finally {
                musicCursor.close();
            }
        }




    }

}
