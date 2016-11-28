package edu.calpoly.recommendo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sethbarrios on 11/27/16.
 */

public class PreferencesManager {
    private static PreferencesManager preferencesManager;
    private ArrayList<String> prefList;
    private static final String TAG = "PreferencesManager";
    private static final String PREF_KEY = "EDU_CALPOLY_RECOMMENDO_PREFERENCES";
    private static final String PREFS_NAME = "edu.calpoly.recommendo";

    /**
     * @return Singleton instance of PreferencesManager
     */
    public static PreferencesManager getPreferencesManager() {
        if (preferencesManager == null) preferencesManager = new PreferencesManager();
        return preferencesManager;
    }

    /**
     * Singleton constructor
     */
    private PreferencesManager() {
        super();
    }

    /**
     * Returns existing preference list
     * If no existing preference list, tries to fetch from persistent memory
     * @return ArrayList of strings
     */
    public ArrayList<String> getPrefList(final Context context) {
        if (prefList != null) return prefList;

        // Try to fetch prefList from persistent memory
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.contains(PREF_KEY)) {
            Gson gson = new Gson();
            String savedVals = prefs.getString(PREF_KEY, null);
            prefList = gson.fromJson(savedVals, new ArrayList<String>().getClass());
        }
        else
        {
            prefList = new ArrayList<String>();
        }
        return prefList;
    }

    /**
     * Converts arrayList to JSON and saves to sharedPreferences
     */
    public void savePrefList(final Context context) {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editor.putString(PREF_KEY, gson.toJson(prefList));
        editor.commit();
    }



}
