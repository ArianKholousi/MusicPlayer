package com.arian.musicplayer;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLyricsDialogFragment extends DialogFragment {

    private Callbacks callbacks;

    public static AddLyricsDialogFragment newInstance() {

        Bundle args = new Bundle();

        AddLyricsDialogFragment fragment = new AddLyricsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddLyricsDialogFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_add_lyrics, null, false);


        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setMessage("No lyrics found for this music")
                .setPositiveButton("Add Lyrics", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbacks.showAddLyricsFragment();
                    }
                }).create();
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

    public interface Callbacks {
       void showAddLyricsFragment();
    }

}
