package edu.calpoly.recommendo.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.calpoly.recommendo.adapters.ImageAdapter;
import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.PreferencesManager;

public class Preferences extends AppCompatActivity {

    private static final String TAG = "Preferences";
    public static final String COFFEE = "coffee";
    public static final String FITNESS = "fitness";
    public static final String RESTAURANT = "restaurant";
    public static final String MOVIES = "movies";
    public static final String HIKING = "hiking";
    public static final String BOWLING = "bowling";
    public static final String READING = "reading";
    public static final String NIGHTCLUB = "nightclub";
    public static final String SHOPPING = "shopping";

    private PreferencesManager preferencesManager;
    private ArrayList<PreferenceItem> preferenceItems;
    private ArrayList<String> savedPrefList;
    private ImageAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // Get preference items to set on adapter
        preferencesManager = PreferencesManager.getPreferencesManager();
        savedPrefList = preferencesManager.getPrefList(this);
        preferenceItems = getPreferenceObjects();

        // Set imageAdapter
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        adapter = new ImageAdapter(this, preferenceItems);
        gridview.setAdapter(adapter);

        //Button doneButton = (Button) findViewById(R.id.preferences_done);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // Decide if user removed or added corresponding preference
                PreferenceItem pItem = preferenceItems.get(position);
                if (pItem.checked) {
                    pItem.checked = false;
                    savedPrefList.remove(pItem.activityDescription);
                } else {
                    pItem.checked = true;
                    savedPrefList.add(pItem.activityDescription);
                }
                adapter.notifyDataSetChanged();
            }
        });

        /*doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch new intent
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save preferences here
        preferencesManager.savePrefList(this);
    }


    private ArrayList<PreferenceItem> getPreferenceObjects() {
        ArrayList<PreferenceItem> icons = new ArrayList<>();

        // Add all icons and their tags
        icons.add(new PreferenceItem(R.drawable.coffee, Preferences.COFFEE, savedPrefList.contains(Preferences.COFFEE)));
        icons.add(new PreferenceItem(R.drawable.fitness, Preferences.FITNESS, savedPrefList.contains(Preferences.FITNESS)));
        icons.add(new PreferenceItem(R.drawable.restaurant, Preferences.RESTAURANT, savedPrefList.contains(Preferences.RESTAURANT)));
        icons.add(new PreferenceItem(R.drawable.movies, Preferences.MOVIES, savedPrefList.contains(Preferences.MOVIES)));
        icons.add(new PreferenceItem(R.drawable.hiking, Preferences.HIKING, savedPrefList.contains(Preferences.HIKING)));
        icons.add(new PreferenceItem(R.drawable.bowling, Preferences.BOWLING, savedPrefList.contains(Preferences.BOWLING)));
        icons.add(new PreferenceItem(R.drawable.reading, Preferences.READING, savedPrefList.contains(Preferences.READING)));
        icons.add(new PreferenceItem(R.drawable.nightclub, Preferences.NIGHTCLUB, savedPrefList.contains(Preferences.NIGHTCLUB)));
        icons.add(new PreferenceItem(R.drawable.shopping, Preferences.SHOPPING, savedPrefList.contains(Preferences.SHOPPING)));

        return icons;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Done:
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
