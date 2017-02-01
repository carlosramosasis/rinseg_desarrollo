package rinseg.asistp.com.utils;

import android.content.SharedPreferences;

/**
 * Created by OSequeiros on 01/02/2017.
 * Clase para el manejo de shared preferences
 */

public class SharedPreferencesHelper implements ISharedPreferencesHelper {

    private final String MY_SHARED_PREFERENCES = "com.asistp.rinseg.";
    private final String KEY_TOKEN = MY_SHARED_PREFERENCES + "session.token";

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(SharedPreferences preferences) {
        sharedPreferences = preferences;
    }

    @Override
    public void saveToken(String token) {
        SharedPreferences.Editor editor = editor();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    @Override
    public void clear() {
        SharedPreferences.Editor editor = editor();
        editor.clear();
        editor.apply();
    }

    private  SharedPreferences.Editor editor() {
        return sharedPreferences.edit();
    }
}
