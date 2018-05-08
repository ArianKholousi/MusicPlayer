package com.arian.musicplayer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFragment extends Fragment {

//    private RecyclerView recyclerView;
//    private StringAdapter stringAdapter;

    private TextView tvLyric;

    private SparseArray<String> lyrics;

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
        lyrics = LyricsPreferences.getStoredList(getActivity(), String.valueOf(currentSong.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        tvLyric = (TextView) view.findViewById(R.id.tv_showing_lyric);
        tvLyric.setText(lyrics.get(mediaPlayer.getCurrentPosition()));

//        recyclerView = (RecyclerView) view.findViewById(R.id.lyrics_recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        updateRecyclerView();

        return view;
    }
//
//    private class StringHolder extends RecyclerView.ViewHolder {
//        private String string;
//        private TextView tvLyric;
//
//        public StringHolder(View itemView) {
//            super(itemView);
//            tvLyric = (TextView) itemView.findViewById(R.id.tv_lyric_line);
//        }
//
//        public void bindString(String string){
//            this.string = string;
//            tvLyric.setText(string);
//        }
//
//    }
//
//    private class StringAdapter extends RecyclerView.Adapter<StringHolder>{
//
//        private List<String> strings;
//
//        public StringAdapter(List<String> strings) {
//            this.strings = strings;
//        }
//
//        @Override
//        public StringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(getActivity()).inflate(R.layout.lyrics_list_item,parent,false);
//            return new StringHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(StringHolder holder, int position) {
//            String string = strings.get(position);
//            holder.bindString(string);
//        }
//
//        @Override
//        public int getItemCount() {
//            return strings.size();
//        }
//    }
//
//
//    private void updateRecyclerView(){
//        if (stringAdapter == null) {
//            stringAdapter = new StringAdapter(lyrics);
//            recyclerView.setAdapter(stringAdapter);
//        }else
//            stringAdapter.notifyDataSetChanged();
//    }

}
