package com.arian.musicplayer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arian.musicplayer.MainActivity.currentSong;
import static com.arian.musicplayer.MediaPlaybackService.mediaPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLyricsFragment extends Fragment implements View.OnClickListener {

//    private List<String> lyrics;
    private SparseArray<String> sparseArrayString ;
    private List<EditText> editTextList;

    private int currentSecond;

    private Button btnAdd;
    private Button btnSubmit;
    private RecyclerView recyclerView;
    private EdittextAdapter edittextAdapter;


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
//        lyrics = new ArrayList<>();
        sparseArrayString =  new SparseArray<>();
        editTextList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_lyrics, container, false);

        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        recyclerView = (RecyclerView) view.findViewById(R.id.add_lyrics_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        btnAdd.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        updateRecyclerView();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                editTextList.add(new EditText(getActivity()));
                currentSecond = mediaPlayer.getCurrentPosition();
                updateRecyclerView();
                break;

            case R.id.btn_submit:
                sparseArrayString.put(currentSecond,editTextList.get(editTextList.size() -1).getText().toString());
                LyricsPreferences.setStoredList(getActivity(),String.valueOf(currentSong.getId()),sparseArrayString);
                Toast.makeText(getActivity(), "Successfully submitted!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private class EdittextHolder extends RecyclerView.ViewHolder {
        private EditText editText;

        public EdittextHolder(View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.et_lyric);
        }

        public void bindEdittext(EditText editText){
            this.editText = editText;
        }

    }

    private class EdittextAdapter extends RecyclerView.Adapter<EdittextHolder>{

        private List<EditText> editTexts;

        public EdittextAdapter(List<EditText> editTexts) {
            this.editTexts = editTexts;
        }

        @Override
        public EdittextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.adding_lyrics_list_item,parent,false);
            return new EdittextHolder(view);
        }

        @Override
        public void onBindViewHolder(EdittextHolder holder, int position) {
            EditText editText = editTexts.get(position);
            holder.bindEdittext(editText);
        }

        @Override
        public int getItemCount() {
            return editTexts.size();
        }
    }


    public void updateRecyclerView(){
        if (edittextAdapter== null){
            edittextAdapter = new EdittextAdapter(editTextList);
            recyclerView.setAdapter(edittextAdapter);
        }else
            edittextAdapter.notifyDataSetChanged();
    }

}
