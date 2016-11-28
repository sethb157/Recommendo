package edu.calpoly.recommendo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    public static ArrayList<String> prefList;
    public static final String BIKING = "biking";
    public static final String COFFEE = "coffee";
    public static final String FITNESS = "fitness";
    public static final String RUNNING = "running";
    public static final String RESTAURANT = "restaurant";
    public static final String SWIMMING = "swimming";
    public static final String MOVIES = "movies";
    public static final String PIZZA = "pizza";
    public static final String HIKING = "hiking";
    public static final String GOLF = "golf";

    private PreferencesManager preferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        preferencesManager = PreferencesManager.getPreferencesManager();
        prefList = preferencesManager.getPrefList(this);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        Button doneButton = (Button) findViewById(R.id.preferences_done);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(Preferences.this, "The position is " + position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    if (prefList.contains(BIKING)) {
                        prefList.remove(BIKING);
                    }
                    else {
                        prefList.add(BIKING);
                    }
                }
                else if (position == 1) {
                    if (prefList.contains(COFFEE)) {
                        prefList.remove(COFFEE);
                    }
                    else {
                        prefList.add(COFFEE);
                    }
                }
                else if (position == 2) {
                    if (prefList.contains(FITNESS)) {
                        prefList.remove(FITNESS);
                    }
                    else {
                        prefList.add(FITNESS);
                    }
                }
                else if (position == 3) {
                    if (prefList.contains(RUNNING)) {
                        prefList.remove(RUNNING);
                    }
                    else {
                        prefList.add(RUNNING);
                    }
                }
                else if (position == 4) {
                    if (prefList.contains(RESTAURANT)) {
                        prefList.remove(RESTAURANT);
                    }
                    else {
                        prefList.add(RESTAURANT);
                    }
                }
                else if (position == 5) {
                    if (prefList.contains(SWIMMING)) {
                        prefList.remove(SWIMMING);
                    }
                    else {
                        prefList.add(SWIMMING);
                    }
                }
                else if (position == 6) {
                    if (prefList.contains(MOVIES)) {
                        prefList.remove(MOVIES);
                    }
                    else {
                        prefList.add(MOVIES);
                    }
                }
                else if (position == 7) {
                    if (prefList.contains(PIZZA)) {
                        prefList.remove(PIZZA);
                    }
                    else {
                        prefList.add(PIZZA);
                    }
                }
                else if (position == 8) {
                    if (prefList.contains(HIKING)) {
                        prefList.remove(HIKING);
                    }
                    else {
                        prefList.add(HIKING);
                    }
                }
                else if (position == 9) {
                    if (prefList.contains(GOLF)) {
                        prefList.remove(GOLF);
                    }
                    else {
                        prefList.add(GOLF);
                    }
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch new intent
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < prefList.size(); i++) {
            Log.d("LOGGING!!!", "Position " + i + ": " + prefList.get(i));
        }

        // Save preferences here
        preferencesManager.savePrefList(this);
    }
}
