package edu.calpoly.recommendo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.calpoly.recommendo.adapters.ImageAdapter;
import edu.calpoly.recommendo.R;

public class Preferences extends AppCompatActivity {

    public static ArrayList<String> prefList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        prefList = (ArrayList<String>) getLastCustomNonConfigurationInstance();

        if (prefList == null) {
            prefList = new ArrayList<String>();
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(Preferences.this, "The position is " + position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    if (prefList.contains("biking")) {
                        prefList.remove("biking");
                    }
                    else {
                        prefList.add("biking");
                    }
                }
                else if (position == 1) {
                    if (prefList.contains("coffee")) {
                        prefList.remove("coffee");
                    }
                    else {
                        prefList.add("coffee");
                    }
                }
                else if (position == 2) {
                    if (prefList.contains("fitness")) {
                        prefList.remove("fitness");
                    }
                    else {
                        prefList.add("fitness");
                    }
                }
                else if (position == 3) {
                    if (prefList.contains("running")) {
                        prefList.remove("running");
                    }
                    else {
                        prefList.add("running");
                    }
                }
            }
        });


    }

    @Override
    public ArrayList<String> onRetainCustomNonConfigurationInstance() {
        return prefList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < prefList.size(); i++) {
            Log.d("LOGGING!!!", "Position " + i + ": " + prefList.get(i));
        }
    }
}
