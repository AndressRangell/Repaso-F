package com.flota.inicializacion.trans_init.trans;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.wposs.flota.R;

/**
 * Created by Julian on 20/06/2018.
 */

public class SharedPreferences {

    public static final String KEY_STAN = "STAN";
    private static String prefsKey;

    private SharedPreferences(){

    }

    public static void saveValueStrPreference(Context context, String key, String value) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveValueIntPreference(Context context, String key, int value) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getValueStrPreference(Context context, String key) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static int getValueIntPreference(Context context, String key) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static void saveValueBooleanPreference(Context context, String key, Boolean value) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getValueBooleanPreference(Context context, String key) {
        prefsKey = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(prefsKey, MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

}
