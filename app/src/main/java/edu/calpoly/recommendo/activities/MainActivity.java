package edu.calpoly.recommendo.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;



import edu.calpoly.recommendo.AddressResultReceiver;
import edu.calpoly.recommendo.FetchAddressIntentService;
import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.weather.scheme.WeatherJSON;
import edu.calpoly.recommendo.suggestions.SuggestionsManager;

public class MainActivity extends AppCompatActivity implements SuggestionsManager.SuggestionListener, AddressResultReceiver.Receiver{

    private final String TAG = "MainActivity";
    private final int REQUEST_LOC_CODE = 242;
    private SuggestionsManager suggestionsManager;

    private TextView cityTextView;
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        suggestionsManager = SuggestionsManager.getSharedManager();


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
    }

    private void updateWeatherDescription(WeatherJSON weatherObject) {
        String weatherDesc = weatherObject.getMain().getTemp().intValue()
                + "\u00B0 - " + weatherObject.getWeather().get(0).getMain();
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

