package edu.calpoly.recommendo.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;


import java.util.ArrayList;

import edu.calpoly.recommendo.AddressResultReceiver;
import edu.calpoly.recommendo.FetchAddressIntentService;
import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.adapters.MyAdapter;
import edu.calpoly.recommendo.managers.PreferencesManager;
import edu.calpoly.recommendo.managers.weather.scheme.WeatherJSON;
import edu.calpoly.recommendo.suggestions.Suggestion;
import edu.calpoly.recommendo.suggestions.SuggestionsManager;

public class MainActivity extends AppCompatActivity implements SuggestionsManager.SuggestionListener, AddressResultReceiver.Receiver{

    private final String TAG = "MainActivity";
    private final int REQUEST_LOC_CODE = 242;
    private SuggestionsManager suggestionsManager;

    private TextView cityTextView;
    private TextView weatherTextView;
    private ImageView weatherImageView;

    private RecyclerView rv;
    private static MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user has preferences
        PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
        if (preferencesManager.getPrefList(this).size() == 0) {
            // Launch activity to select preferences
            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_main:

                                break;
                            case R.id.action_map:
                                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                                startActivity(mapIntent);
                                break;
                            case R.id.action_pref:
                                Intent prefIntent = new Intent(getApplicationContext(), Preferences.class);
                                startActivity(prefIntent);
                                break;
                        }
                        return false;
                    }
                }
        );

        // Get UI handles
        cityTextView = (TextView)findViewById(R.id.city_name_text_view);
        weatherTextView = ((TextView) findViewById(R.id.weather_desc_text_view));
        weatherImageView = (ImageView) findViewById(R.id.weather_image_view);

        suggestionsManager = SuggestionsManager.getSharedManager();

        fetchNewDataIfPossible();

        rv = (RecyclerView) findViewById(R.id.rv);
        assert rv != null;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdapter();
        rv.setAdapter(adapter);
        //String x = mSuggestions.get(0).getName();

    }


    @Override
    protected void onResume() {
        super.onResume();
        suggestionsManager.addListener(this);
        fetchNewDataIfPossible();
    }

    @Override
    protected void onPause() {
        super.onPause();
        suggestionsManager.removeListener(this);
    }

    private void fetchNewDataIfPossible() {
        if (!suggestionsManager.locationEnabled(this)) {
            requestLocationPermission();
        }
        else {
            suggestionsManager.fetchNewData(this);
        }
    }

    /*START PERMISSION REQUEST*/
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOC_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOC_CODE:
                if (grantResults.length > 1 && grantResults[0] == 0 && grantResults[1] == 0)
                    suggestionsManager.fetchNewData(this);
                break;
            default:
                break;
        }
    }

    /*END PERMISSION REQUEST*/

    @Override
    public void newDataFetched() {
        updateWeatherDescription(suggestionsManager.getLastWeatherRetrieved());
        updateCityName(suggestionsManager.getLastLocation());
        updateWeatherIcon(suggestionsManager.getLastWeatherRetrieved().getWeather().get(0).getIcon());

        adapter.mSuggestions = suggestionsManager.getSuggestions();
        adapter.notifyDataSetChanged();
    }


    /**
     *
     * @param iconID uses iconID to select icon)
     */
    private void updateWeatherIcon(String iconID) {
        // Cloudy
        if (iconID.startsWith("02") || iconID.startsWith("03") || iconID.startsWith("04") || iconID.startsWith("50")) {
            weatherImageView.setImageResource(R.drawable.ic_cloud_queue_white_24dp);
        }
        // Rainy
        else if (iconID.startsWith("09") || iconID.startsWith("10") || iconID.startsWith("11")) {
            weatherImageView.setImageResource(R.drawable.rain);
        }
        // Sunny
        else {
            // Day or night
            if (iconID.contains("d")) {
                weatherImageView.setImageResource(R.drawable.ic_wb_sunny_white_48dp);
            }
            else {
                weatherImageView.setImageResource(R.drawable.moon);
            }
        }

    }

    private void updateWeatherDescription(WeatherJSON weatherObject) {
        String weatherDesc = weatherObject.getMain().getTemp().intValue()
                + "\u00B0 - " + weatherObject.getWeather().get(0).getDescription();
        weatherTextView.setText(weatherDesc, TextView.BufferType.EDITABLE);
    }

    private AddressResultReceiver addressResultReceiver;

    /**
     * Starts a service to get city name
     * @param location
     */
    private void updateCityName(Location location) {
        Log.d(TAG, "cityNameForLocation: Started");
        addressResultReceiver = new AddressResultReceiver(new Handler());
        addressResultReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FetchAddressIntentService.class);

        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, addressResultReceiver);

        startService(intent);
    }

    /**
     * This is specifically for address results
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // Update UI if city fetch was successful
        if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
            String cityResult = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY, "");
            if (!cityResult.isEmpty()) cityTextView.setText(cityResult, TextView.BufferType.EDITABLE);
        }
    }

}

