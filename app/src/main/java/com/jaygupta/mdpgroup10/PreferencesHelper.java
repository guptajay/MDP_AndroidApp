package com.jaygupta.mdpgroup10;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "ahhsaushhuuashu";
    private static final String KEY = "myKey";

    public static void saveData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, TEXT);
        editor.apply();
    }

    public static String loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, context.MODE_PRIVATE);
        String text = sharedPreferences.getString(KEY, "");
        return text;
    }

}
