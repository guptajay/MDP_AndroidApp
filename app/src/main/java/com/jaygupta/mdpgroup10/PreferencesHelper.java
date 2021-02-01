package com.jaygupta.mdpgroup10;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String SHARED_PREFS = "sharedPrefs";

    public static void saveData(Context context, String f, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, f);
        editor.commit();
    }

    public static String loadData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String text = sharedPreferences.getString(key, "Not Found");
        return text;
    }

}
