package com.arian.musicplayer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLyricsFragment extends Fragment implements View.OnClickListener {

    private SparseArray<String> map;
    private List<String> stringList;

    private int currentSecond;

    private Button btnAdd;
    private EditText editText;
    private RecyclerView recyclerView;
    private StringAdapter stringAdapter;


    public static AddLyricsFragment newInstance() {

        Bundle args = new Bundle();

        AddLyricsFragment fragment = new AddLyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public AddLyricsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map =  new SparseArray<>();
        stringList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_lyrics, container, false);

        editText = (EditText) view.findViewById(R.id.et_edittext_add_lyric);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        recyclerView = (RecyclerView) view.findViewById(R.id.add_lyrics_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAdd.setOnClickListener(this);

        updateRecyclerView();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                currentSecond = mediaPlayer.getCurrentPosition()/1000;
                if (editText.getText().toString().length()!= 0) {
                    map.put(currentSecond, String.valueOf(editText.getText()));
                    Log.d("mytag6", String.valueOf(editText.getText()));
                    stringList.add(String.valueOf(editText.getText()));
                    editText.getText().clear();
                    LyricsPreferences.setStoredList(getActivity(),String.valueOf(currentSong.getId()), map);
                    updateRecyclerView();
                }
                break;
        }
    }



    private class StringHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private String string;

        public StringHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_lyric);
        }

        public void bindText(String string){
            this.string = string;
            textView.setText(string);
        }

//        @Override
//        public boolean onLongClick(View v) {
//            Log.d("mytag7", String.valueOf(map.size()));
//            map.removeAt(map.indexOfValue(string));
//            stringList.remove(string);
//            LyricsPreferences.setStoredList(getActivity(),String.valueOf(currentSong.getId()), map);
//            updateRecyclerView();
//            return true;
//        }
    }

    private class StringAdapter extends RecyclerView.Adapter<StringHolder>{

        private List<String> strings;

        public StringAdapter(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public StringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.adding_lyrics_list_item,parent,false);
            return new StringHolder(view);
        }

        @Override
        public void onBindViewHolder(StringHolder holder, int position) {
            String string = strings.get(position);
            holder.bindText(string);
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }
    }


    public void updateRecyclerView(){
        if (stringAdapter == null){
            stringAdapter = new StringAdapter(stringList);
            recyclerView.setAdapter(stringAdapter);
        }else
            stringAdapter.notifyDataSetChanged();
    }

}
