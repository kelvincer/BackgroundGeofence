package com.home.geofencebackground;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 ** Created by rcaroama on 04/11/2016.
 */

public class PreferenceManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "club";

    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String value) {
        return pref.getString(key, value);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean value) {

        return pref.getBoolean(key, value);
    }

    public void putInt(String key, int value) {

        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int value) {

        return pref.getInt(key, value);
    }

    public void clearKeyPreference(String key){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }
}
