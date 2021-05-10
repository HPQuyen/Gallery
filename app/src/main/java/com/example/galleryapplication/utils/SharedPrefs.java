package com.example.galleryapplication.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.galleryapplication.classes.App;

public class SharedPrefs {

    public static final String LANGUAGE = "language";
    public static final String DARKTHEME = "theme_dark";

    public static final String VIEWALLLAYOUT = "viewall_layout";
    public static final String ALBUMLAYOUT = "album_layout";
    public static final String FAVORITELAYOUT = "favorite_layout";

    private static SharedPrefs instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefs() {
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(App.self().getApplicationContext());
    }

    public static SharedPrefs getInstance() {
        if (instance == null) {
            instance = new SharedPrefs();
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) sharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(sharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(sharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(sharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(sharedPreferences.getLong(key, 0));
        } else {
            return App.self()
                    .getGSon()
                    .fromJson(sharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, App.self().getGSon().toJson(data));
        }
        editor.apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

}
