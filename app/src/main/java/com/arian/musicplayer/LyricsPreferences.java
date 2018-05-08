package com.arian.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payami on 05/04/2018.
 */

public class LyricsPreferences {

//    public static final String PREF_LYRICS = "pref_lyrics";

    public static SparseArray<String> getStoredList(Context context, String key) {
        Gson gson = new Gson();
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
        Type type = new TypeToken<SparseArray<String>>() {
        }.getType();

        return gson.fromJson(json, type);
    }


    public static void setStoredList(Context context, String key, SparseArray<String> sparseArray) {
        Gson gson = new Gson();
        String json = gson.toJson(sparseArray);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key,json).apply();
    }
}
