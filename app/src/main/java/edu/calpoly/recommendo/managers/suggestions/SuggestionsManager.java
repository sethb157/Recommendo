package edu.calpoly.recommendo.managers.suggestions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import edu.calpoly.recommendo.activities.Preferences;
import edu.calpoly.recommendo.managers.PreferencesManager;
import edu.calpoly.recommendo.managers.places.PlacesFetcher;
import edu.calpoly.recommendo.managers.places.scheme.Photo;
import edu.calpoly.recommendo.managers.places.scheme.PlacesResult;
import edu.calpoly.recommendo.managers.places.scheme.Result;
import edu.calpoly.recommendo.managers.weather.WeatherFetcher;
import edu.calpoly.recommendo.managers.weather.scheme.WeatherJSON;

/**
 * Created by Dan on 11/22/2016.
 */

public class SuggestionsManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, WeatherFetcher.WeatherFetcherListener, PlacesFetcher.PlacesFetcherListener {
    private static final String TAG = "SuggestionsManager";
    private static SuggestionsManager suggestionsManager;

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

    private PreferencesManager preferencesManager;
    private Context preferencesContext;

    private static ArrayList<Suggestion> mSuggestions;
    public static ArrayList<Suggestion> getSuggestions() {
        return mSuggestions;
    }

    private ArrayList<SuggestionListener> listeners = new ArrayList<>();
    public void addListener(SuggestionListener listener) {listeners.add(listener);}
    public void removeListener(SuggestionListener listener) {listeners.remove(listener);}

    private PlacesFetcher placesFetcher;

    // Private for singleton purposes
    private SuggestionsManager() {
        super();

        preferencesManager = PreferencesManager.getPreferencesManager();

        placesFetcher = new PlacesFetcher();
        placesFetcher.listener = this;
    }

    /**
     *
     * @return Singleton instance
     */
    public static SuggestionsManager getSharedManager() {
        if (suggestionsManager == null) suggestionsManager = new SuggestionsManager();
        return suggestionsManager;
    }


    /**
     * Updates places fetcher and then fetches new places
     */
    public void updateSuggestions() {
        placesFetcher.longitude = Double.toString(lastLocation.getLongitude());
        placesFetcher.latitude = Double.toString(lastLocation.getLatitude());
        placesFetcher.placeTypes = getSearchTerms(lastWeatherRetrieved.getMain().getTemp(), false);
        placesFetcher.fetchPlaces();
    }

    @Override
    public void placesFetchFinished(PlacesFetcher fetcher, ArrayMap<String, PlacesResult> fetchResults) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();

        // Add clothing suggestions first
        addClothing(suggestions, lastWeatherRetrieved.getMain().getTemp(), false);

        // Parse through fetch results and generate new suggestions
        for (String searchTerm : fetchResults.keySet()) {
            PlacesResult placesResult = fetchResults.get(searchTerm);
            for (Result result : placesResult.getResults()) {

                // Get first photo ref
                String photoRef = "";
                List<Photo> photos = result.getPhotos();
                if (!photos.isEmpty()) {
                    photoRef = photos.get(0).getPhotoReference();
                }

                Suggestion newSuggestion
                        = new Suggestion(result.getName(), result.getVicinity(), null, TYPE_ACTIVITY, searchTerm, photoRef,
                        result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                suggestions.add(newSuggestion);
            }
        }

        // Set new suggestions and alert all listeners of change
        mSuggestions = suggestions;
        for (SuggestionListener listener : listeners) {
            listener.newDataFetched();
        }
    }

    /**
     * Call this function to get new weather and suggestions, based on location
     */
    public void fetchNewData(final Context context) {
        if (preferencesContext == null) preferencesContext = context.getApplicationContext();

        // When new data is requested, fetch new location
        // A new location will trigger new weather data being fetched
        fetchLocation(context);
    }

    /* Location Services*/
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final int numSecondsRefresh = 600;
    private Location lastLocation;
    public Location getLastLocation() {return lastLocation;}

    /**
     * If locations are not being fetched, this begins fetching
     * Location updates are done via the suggestion listener interface
     */

    private void fetchLocation(final Context context) {
        // Either begin fetching locations or notify of last location found
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        } else if (lastLocation != null) {
            // Simulate a location change so that new data can be fetched
            onLocationChanged(lastLocation);
        }
    }

    /**
     * Helper function for verbose permission garbage
     * @return Value indicates whether permissions have been granted
     */
    public boolean locationEnabled(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(1000 * numSecondsRefresh);

        //noinspection MissingPermission
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
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
        // Only update data if serious location change
        if (lastLocation == null || (lastLocation != null && location.distanceTo(lastLocation) >= 500)) {
            lastLocation = location;
            fetchWeather();
        }
    }


    public class LocationServicesNotEnabledException extends Exception {
        public LocationServicesNotEnabledException(String message) {
            super(message);
        }

    }

    /* End location stuff*/



    /* Begin Weather*/
    private WeatherFetcher weatherFetcher;
    private WeatherJSON lastWeatherRetrieved;
    public WeatherJSON getLastWeatherRetrieved(){return lastWeatherRetrieved;}


    /**
     * Updates WeatherFetcher with current location and then fetches new Weather result
     * Relays update via callback methods from SuggestionsManager
     * This should not be called if no weather has been fetched
     */
    private void fetchWeather() {
        if (weatherFetcher == null) {
            weatherFetcher = new WeatherFetcher();
            weatherFetcher.listener = this;
        }
        weatherFetcher.latitude = Double.toString(lastLocation.getLatitude());
        weatherFetcher.longitude = Double.toString(lastLocation.getLongitude());
        weatherFetcher.fetchWeather();
    }

    @Override
    public void weatherFetchFailed() {
        Log.d(TAG, "weatherFetchFailed: ");
    }

    @Override
    public void weatherFetchSucceeded(WeatherJSON response) {
        // Save last weather retrieved and update suggestions now
        Log.d(TAG, "weatherFetchSucceeded: ");
        lastWeatherRetrieved = response;
        updateSuggestions();
    }
    /*End Weather*/


    private ArrayList<String> getSearchTerms(double avgTemp, boolean rainOrSnow) {
        ArrayList<String> prefList = preferencesManager.getPrefList(preferencesContext);
        ArrayList<String> searchTerms = new ArrayList<>();
        if (prefList == null) return searchTerms;
        if (avgTemp <= 30) {
            if (prefList.contains(Preferences.MOVIES)) {
                searchTerms.add("movie_rental");
                searchTerms.add("movie_theater");
            }
            if (prefList.contains(Preferences.COFFEE)) {
                searchTerms.add("cafe");
            }

        }
        else if (avgTemp <= 55) {
            if (prefList.contains(Preferences.COFFEE)) {
                searchTerms.add("cafe");
            }
            if (prefList.contains(Preferences.FITNESS)) {
                searchTerms.add("gym");

            }
            if (prefList.contains(Preferences.MOVIES)) {
                searchTerms.add("movie_rental");
                searchTerms.add("movie_theater");
            }
            if (!rainOrSnow) {
                if (prefList.contains(Preferences.RESTAURANT)) {
                    searchTerms.add("restaurant");
                }
            }
        }
        else if (avgTemp <= 70) {
            if (prefList.contains(Preferences.COFFEE)) {
                searchTerms.add("cafe");
            }
            if (prefList.contains(Preferences.FITNESS)) {
                searchTerms.add("gym");

            }
            if (prefList.contains(Preferences.MOVIES)) {
                searchTerms.add("movie_rental");
                searchTerms.add("movie_theater");
            }
            if (!rainOrSnow) {
                if (prefList.contains(Preferences.RESTAURANT)) {
                    searchTerms.add("restaurant");
                }
            }

        }
        else if (avgTemp <= 85) {

            if (prefList.contains(Preferences.HIKING)) {
                //TODO: Add search terms to placesAPI

            }

//            if (!rainOrSnow) {
//                if (prefList.contains(Preferences.BIKING)) {
//                    // park
//
//                }
//                if (prefList.contains(Preferences.SWIMMING)) {
//                    // search: swimming pool
//
//                }
//                if (prefList.contains(Preferences.GOLF)) {
//                    // search: golf
//
//                }
//            }
            if (prefList.contains(Preferences.RESTAURANT)) {
                searchTerms.add("restaurant");
            }
            if (prefList.contains(Preferences.FITNESS)) {
                searchTerms.add("gym");

            }
            if (prefList.contains(Preferences.MOVIES)) {
                searchTerms.add("movie_rental");
                searchTerms.add("movie_theater");

            }


        }
        else {
            searchTerms.add("park");
//            if (prefList.contains(Preferences.SWIMMING)) {
//                // search: swimming pool
//
//            }
//            if (prefList.contains(Preferences.GOLF)) {
//                // search: golf
//
//            }
//            if (prefList.contains(Preferences.BIKING)) {
//                // park
//
//            }
//            if (prefList.contains(Preferences.HIKING)) {
//                // search: hiking area
//
//            }
//            if (prefList.contains(Preferences.RESTAURANT)) {
//                // restaurant
//
//            }
//            if (prefList.contains(Preferences.FITNESS)) {
//                // gym
//
//            }
//            if (prefList.contains(Preferences.RUNNING)) {
//                // park
//                // radius: 1 mile away
//
//            }
        }

        // If user has no tastes, add some default options
        if (searchTerms.isEmpty()) {
            searchTerms.add("liquor_store");
            searchTerms.add("police");
        }

        return searchTerms;
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
        void newDataFetched();
    }

}
