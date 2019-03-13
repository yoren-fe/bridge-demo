package com.example.androidandflutterforandroid.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private static SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_NAME = "FlutterSharedPreferences";
    private static final StringBuilder KEY_PREFIX = new StringBuilder("flutter.");

    public LocalStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, context.MODE_PRIVATE);
    }

    public  void putString(String key, String value) {
        sharedPreferences.edit().putString(KEY_PREFIX.append(key).toString(), value).apply();
    }

    public  void getString(String key) {
        sharedPreferences.getString(KEY_PREFIX.append(key).toString(), "");
    }
}
