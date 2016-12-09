package edu.calpoly.recommendo.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import edu.calpoly.recommendo.adapters.ClothingAdapter;
import edu.calpoly.recommendo.adapters.SuggestionsFirstLevelAdapter;
import edu.calpoly.recommendo.managers.AddressResultReceiver;
import edu.calpoly.recommendo.managers.FetchAddressIntentService;
import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.PreferencesManager;
import edu.calpoly.recommendo.managers.weather.scheme.WeatherJSON;
import edu.calpoly.recommendo.managers.suggestions.SuggestionsManager;

public class MainActivity extends AppCompatActivity implements SuggestionsManager.SuggestionListener, AddressResultReceiver.Receiver{

    private final String TAG = "MainActivity";
    private final int REQUEST_LOC_CODE = 242;
    private SuggestionsManager suggestionsManager;


    private ImageView weatherImageView;
    private FrameLayout progressBarHolder;

    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private TextView temperatureTextView;

    private RecyclerView rv;
    private SuggestionsFirstLevelAdapter adapter;

    private RecyclerView clothingRecyclerView;
    private ClothingAdapter clothingAdapter;

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
                                if (progressBarHolder != null && progressBarHolder.getVisibility() == View.GONE) {
                                    if (suggestionsManager != null) {
                                        suggestionsManager.updateSuggestions();
                                    }
                                }
                                break;
                            case R.id.action_map:
                                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                                startActivity(mapIntent);
                                break;
                            case R.id.action_pref:
                                if (progressBarHolder != null && progressBarHolder.getVisibility() == View.GONE) {
                                    Intent prefIntent = new Intent(getApplicationContext(), Preferences.class);
                                    startActivityForResult(prefIntent, 0);
                                }
                                break;
                        }
                        item.setChecked(false);
                        return true;
                    }
                }
        );

        // Get UI handles
        weatherImageView = (ImageView) findViewById(R.id.weather_image_view);
        temperatureTextView = (TextView) findViewById(R.id.temperature_text_view);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        // Clothing recyclerview
        clothingRecyclerView = (RecyclerView) findViewById(R.id.clothing_recycler_view);
        clothingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        clothingAdapter = new ClothingAdapter();
        clothingRecyclerView.setAdapter(clothingAdapter
        );

        // Regular recyclerview
        rv = (RecyclerView) findViewById(R.id.rv);
        assert rv != null;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new SuggestionsFirstLevelAdapter();
        rv.setAdapter(adapter);

        suggestionsManager = SuggestionsManager.getSharedManager();

        // If suggestions already exist, trigger their retrieval manually
        if (suggestionsManager.getSuggestions() != null) {
            this.newDataFetched();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        suggestionsManager.addListener(this);
        if (suggestionsManager.getSuggestions() == null) {
            if (suggestionsManager.locationEnabled(this)) {
                suggestionsManager.fetchNewData(this);
            } else {
                requestLocationPermission();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        suggestionsManager.removeListener(this);
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
        progressBarHolder.bringToFront();
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        updateWeatherText(suggestionsManager.getLastWeatherRetrieved());
        updateWeatherIcon(suggestionsManager.getLastWeatherRetrieved().getWeather().get(0).getIcon());

        adapter.setSuggestionLists(suggestionsManager.getSuggestionsByCategory());
        adapter.setKeysInOrder(suggestionsManager.getKeysInOrder());
        adapter.notifyDataSetChanged();

        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        clothingAdapter.setSuggestions(suggestionsManager.getClothingSuggestions());
        clothingAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (suggestionsManager != null) {
            suggestionsManager.updateSuggestions();
        }
        MainActivity.this.newDataFetched();
    }

    /**
     *
     * @param iconID uses iconID to select icon)
     */
    private void updateWeatherIcon(String iconID) {

        Integer imageResource;
        // Cloudy
        if (iconID.startsWith("02") || iconID.startsWith("03") || iconID.startsWith("04") || iconID.startsWith("50")) {
            imageResource = R.drawable.ic_cloud_queue_white_24dp;
        }
        // Rainy
        else if (iconID.startsWith("09") || iconID.startsWith("10") || iconID.startsWith("11")) {
            imageResource = R.drawable.rain;
        }
        // Sunny
        else {
            // Day or night
            if (iconID.contains("d")) {
                imageResource = R.drawable.ic_wb_sunny_white_48dp;
            }
            else {
                imageResource = R.drawable.moon;
            }
        }
        Glide.with(this).load(imageResource).into(weatherImageView);
    }

    private void updateWeatherText(WeatherJSON weatherObject) {
        String temperature = "" + weatherObject.getMain().getTemp().intValue() + "\u00B0";
        temperatureTextView.setText(temperature);
    }

//    private void updateWeatherDescription(WeatherJSON weatherObject) {
//        String weatherDesc = weatherObject.getMain().getTemp().intValue()
//                + "\u00B0 - " + weatherObject.getWeather().get(0).getDescription();
//        weatherTextView.setText(weatherDesc, TextView.BufferType.EDITABLE);
//    }

    private AddressResultReceiver addressResultReceiver;

    /**
     * Starts a service to get city name
     * @param location
     */
//    private void updateCityName(Location location) {
//        Log.d(TAG, "cityNameForLocation: Started");
//        addressResultReceiver = new AddressResultReceiver(new Handler());
//        addressResultReceiver.setReceiver(this);
//
//        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FetchAddressIntentService.class);
//
//        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location);
//        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, addressResultReceiver);
//
//        startService(intent);
//    }
//
    /**
     * This is specifically for address results
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // Update UI if city fetch was successful
        if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
            String cityResult = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY, "");
//            if (!cityResult.isEmpty()) cityTextView.setText(cityResult, TextView.BufferType.EDITABLE);
        }
    }

}

