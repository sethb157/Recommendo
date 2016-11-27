package edu.calpoly.recommendo.activities;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.suggestions.SuggestionsManager;

public class MainActivity extends AppCompatActivity implements SuggestionsManager.SuggestionListener {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuggestionsManager suggestionManager = new SuggestionsManager();
        if (!suggestionManager.locationEnabled(this)) {
            Log.d(TAG, "onCreate: not enabled");
        }
        else {
            try {
                suggestionManager.fetchLocation(this);
            } catch (SuggestionsManager.LocationServicesNotEnabledException locationServicesNotEnabledExeception) {
                locationServicesNotEnabledExeception.printStackTrace();
            }
        }
    }

    @Override
    public void locationChanged(Location location) {
        Log.d(TAG, "locationChanged: ");
    }
}
