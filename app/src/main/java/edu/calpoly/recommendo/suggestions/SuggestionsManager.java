package edu.calpoly.recommendo.suggestions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import edu.calpoly.recommendo.activities.Preferences;

/**
 * Created by Dan on 11/22/2016.
 */

public class SuggestionsManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private static final String TAG = "SuggestionsManager";
    
    public static String TYPE_CLOTHES = "clothing";
    public static String TYPE_ACTIVITY = "activity";

    public static String HEADWEAR = "headwear";
    public static String TOP = "top";
    public static String BOTTOM = "bottom";

    public static String HEADWEAR_BEANIE = "Beanie";
    public static String HEADWEAR_HAT = "Hat";
    public static String HEADWEAR_UMBRELLA = "Umbrella";
    public static String TOP_SNOWJACKET = "Snow Jacket";
    public static String TOP_RAINJACKET = "Rain Jacket";
    public static String TOP_SWEATSHIRT = "Sweatshirt";
    public static String TOP_LONGSLEEVE = "Long Sleeve Shirt";
    public static String TOP_TSHIRT = "T-Shirt";
    public static String TOP_TANKTOP = "Tank Top";
    public static String BOTTOM_PANTS = "Pants";
    public static String BOTTOM_SHORTS = "Shorts";
    public static String BOTTOM_SNOWPANTS = "Snow Pants";

    private static ArrayList<Suggestion> mSuggestions;

    public static ArrayList<Suggestion> getSuggestions() {
        return mSuggestions != null ? mSuggestions : new ArrayList<Suggestion>();
    }

    private ArrayList<SuggestionListener> listeners = new ArrayList<>();
    public void addListener(SuggestionListener listener) {listeners.add(listener);}
    public void removeListener(SuggestionListener listener) {
        listeners.remove(listener);
    }

    public static void updateSuggestions() {
//        Weather weather = WeatherManager.getWeather();
//        double temperature = weather.getTemperature();
//        double minTemp = weather.getMinTemp();
//        double maxTemp = weather.getMaxTemp();
//        boolean rainOrSnow = weather.IsRainy();


        double temperature = 67; // HARD CODED
        double maxTemp = 89.4; // HARD CODED
        double minTemp = 55; // HARD CODED
        double avgTemp = (maxTemp + minTemp) / 2;
        boolean rainOrSnow = false; // HARD CODED

        ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
        ArrayList<String> prefList = Preferences.prefList;

        addClothing(suggestions, avgTemp, rainOrSnow);
        addActivities(suggestions, avgTemp, rainOrSnow);

        mSuggestions = suggestions;
    }




    /* Location Services*/
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private final int numSecondsRefresh = 600;

    /**
     * If locations are not being fetched, this begins fetching
     * Location updates are done via the suggestion listener interface
     */

    public void fetchLocation(final Context context) throws LocationServicesNotEnabledException {
        // Make sure location can be fetched
        if (!locationEnabled(context)) throw new LocationServicesNotEnabledException("Location services are not enabled. Please request them before calling this");

        // Either begin fetching locations or notify of last location found
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
        else if (lastLocation != null) {
            for (SuggestionListener listener : listeners) {
                listener.locationChanged(lastLocation);
            }
        }
    }

    /**
     * Helper function for verbose permission garbage
     * @return Value indicates whether permissions have been granted
     */
    public boolean locationEnabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(1000 * numSecondsRefresh);

        //noinspection MissingPermission because the application should crash if this code has been reached without permission
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        for (SuggestionListener listener : listeners) {
            listener.locationChanged(location);
        }
    }


    public class LocationServicesNotEnabledException extends Exception {
        public LocationServicesNotEnabledException(String message) {
            super(message);
        }

    }

    /* End location stuff*/




//    if (prefList.contains(Preferences.BIKING)) {
//        // park
//
//    }
//    if (prefList.contains(Preferences.COFFEE)) {
//        // cafe
//
//    }
//    if (prefList.contains(Preferences.FITNESS)) {
//        // gym
//
//    }
//    if (prefList.contains(Preferences.GOLF)) {
//        // search: golf
//
//    }
//    if (prefList.contains(Preferences.HIKING)) {
//        // search: hiking area
//
//    }
//    if (prefList.contains(Preferences.MOVIES)) {
//        // movie_rental
//        // movie_theater
//
//    }
//    if (prefList.contains(Preferences.PIZZA)) {
//        // meal_delivery
//        // meal_takeaway
//
//    }
//    if (prefList.contains(Preferences.RESTAURANT)) {
//        // restaurant
//
//    }
//    if (prefList.contains(Preferences.RUNNING)) {
//        // park
//        // radius: 1 mile away
//
//    }
//    if (prefList.contains(Preferences.SWIMMING)) {
//        // search: swimming pool
//
//    }
    private static void addActivities(ArrayList<Suggestion> suggestions, double avgTemp, boolean rainOrSnow) {
        ArrayList<String> prefList = Preferences.prefList;
        if (prefList == null) return;
        if (avgTemp <= 30) {
            if (prefList.contains(Preferences.MOVIES)) {
                // movie_rental
                // movie_theater

            }
            if (prefList.contains(Preferences.COFFEE)) {
                // cafe

            }

            if (prefList.contains(Preferences.PIZZA)) {
                // meal_delivery
                // meal_takeaway

            }
        }
        else if (avgTemp <= 55) {
            if (prefList.contains(Preferences.COFFEE)) {
                // cafe

            }
            if (prefList.contains(Preferences.FITNESS)) {
                // gym

            }
            if (prefList.contains(Preferences.MOVIES)) {
                // movie_rental
                // movie_theater

            }
            if (!rainOrSnow) {
                if (prefList.contains(Preferences.RESTAURANT)) {
                    // restaurant

                }
            }
            if (prefList.contains(Preferences.PIZZA)) {
                // meal_delivery
                // meal_takeaway

            }
        }
        else if (avgTemp <= 70) {
            if (prefList.contains(Preferences.COFFEE)) {
                // cafe

            }
            if (prefList.contains(Preferences.FITNESS)) {
                // gym

            }
            if (prefList.contains(Preferences.MOVIES)) {
                // movie_rental
                // movie_theater

            }
            if (!rainOrSnow) {
                if (prefList.contains(Preferences.RUNNING)) {
                    // park
                    // radius: 1 mile away

                }
                if (prefList.contains(Preferences.RESTAURANT)) {
                    // restaurant

                }
            }
            if (prefList.contains(Preferences.PIZZA)) {
                // meal_delivery
                // meal_takeaway

            }
        }
        else if (avgTemp <= 85) {

            if (prefList.contains(Preferences.HIKING)) {
                // search: hiking area

            }
            if (prefList.contains(Preferences.RUNNING)) {
                // park
                // radius: 1 mile away

            }
            if (!rainOrSnow) {
                if (prefList.contains(Preferences.BIKING)) {
                    // park

                }
                if (prefList.contains(Preferences.SWIMMING)) {
                    // search: swimming pool

                }
                if (prefList.contains(Preferences.GOLF)) {
                    // search: golf

                }
            }
            if (prefList.contains(Preferences.RESTAURANT)) {
                // restaurant

            }
            if (prefList.contains(Preferences.FITNESS)) {
                // gym

            }
            if (prefList.contains(Preferences.MOVIES)) {
                // movie_rental
                // movie_theater

            }
            if (prefList.contains(Preferences.PIZZA)) {
                // meal_delivery
                // meal_takeaway

            }

        }
        else {
            if (prefList.contains(Preferences.SWIMMING)) {
                // search: swimming pool

            }
            if (prefList.contains(Preferences.GOLF)) {
                // search: golf

            }
            if (prefList.contains(Preferences.BIKING)) {
                // park

            }
            if (prefList.contains(Preferences.HIKING)) {
                // search: hiking area

            }
            if (prefList.contains(Preferences.RESTAURANT)) {
                // restaurant

            }
            if (prefList.contains(Preferences.FITNESS)) {
                // gym

            }
            if (prefList.contains(Preferences.RUNNING)) {
                // park
                // radius: 1 mile away

            }
        }
    }

    private static void addClothing(ArrayList<Suggestion> suggestions, double avgTemp, boolean rainOrSnow) {
        if (avgTemp <= 30) {
            suggestions.add(new Suggestion(HEADWEAR_BEANIE, null, null, TYPE_CLOTHES, HEADWEAR));
            suggestions.add(new Suggestion(TOP_SNOWJACKET, null, null, TYPE_CLOTHES, TOP));
            suggestions.add(new Suggestion(BOTTOM_SNOWPANTS, null, null, TYPE_CLOTHES, BOTTOM));
        }
        else if (avgTemp <= 55) {
            if (rainOrSnow) {
                suggestions.add(new Suggestion(HEADWEAR_UMBRELLA, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_RAINJACKET, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_PANTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
            else {
                suggestions.add(new Suggestion(TOP_SWEATSHIRT, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_PANTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
        }
        else if (avgTemp <= 70) {
            if (rainOrSnow) {
                suggestions.add(new Suggestion(HEADWEAR_UMBRELLA, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_RAINJACKET, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_PANTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
            else {
                suggestions.add(new Suggestion(TOP_LONGSLEEVE, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_PANTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
        }
        else if (avgTemp <= 85) {
            if (rainOrSnow) {
                suggestions.add(new Suggestion(HEADWEAR_UMBRELLA, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_TSHIRT, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_PANTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
            else {
                suggestions.add(new Suggestion(HEADWEAR_HAT, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_TSHIRT, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_SHORTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
        }
        else {
            if (rainOrSnow) {
                suggestions.add(new Suggestion(HEADWEAR_UMBRELLA, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_TANKTOP, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_SHORTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
            else {
                suggestions.add(new Suggestion(HEADWEAR_HAT, null, null, TYPE_CLOTHES, HEADWEAR));
                suggestions.add(new Suggestion(TOP_TANKTOP, null, null, TYPE_CLOTHES, TOP));
                suggestions.add(new Suggestion(BOTTOM_SHORTS, null, null, TYPE_CLOTHES, BOTTOM));
            }
        }

    }

    public interface SuggestionListener {
        void locationChanged(Location location);
    }

}
