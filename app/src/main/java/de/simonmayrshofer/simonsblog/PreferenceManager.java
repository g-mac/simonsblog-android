package de.simonmayrshofer.simonsblog;

import android.content.Context;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PreferenceManager {

    public final static String PREFS_EMAIL = "PREFS_EMAIL";
    public final static String PREFS_PASSWORD = "PREFS_PASSWORD";
    public final static String PREFS_TOKEN = "PREFS_TOKEN";
    public final static String PREFS_LAST_ARTICLES_UPDATE = "PREFS_LAST_ARTICLES_UPDATE";

//    https://developer.android.com/reference/android/content/SharedPreferences.Editor.html
//        preferences.edit().putString(key, value).commit();
//        preferences.edit().putBoolean(key,value).commit();
//        preferences.edit().remove(key).commit
//        preferences.getString(key, default);
//        preferences.getBoolean(key, default)

    public static String getString(Context context, String key) {
        final SharedPreferences preferences = getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(key, null);
    }

    public static boolean putString(Context context, String key, String value) {
        final SharedPreferences preferences = getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.edit().putString(key, value).commit();
    }

    public static void deleteString(Context context, String key) {
        final SharedPreferences preferences = getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().remove(key).commit();
    }

    public static boolean isLoggedIn(Context context) {
        final SharedPreferences preferences = getDefaultSharedPreferences(context.getApplicationContext());
        return !preferences.getString(PREFS_TOKEN, "").isEmpty();
    }

}
