package edu.calpoly.recommendo.managers.suggestions;

import android.Manifest;
import android.app.Presentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.Preference;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.calpoly.recommendo.R;
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

    // Define mapping for icons inline
    private static ArrayMap<String, Integer> nameImageMapping;
    public static Integer getResourceIDForName(String name){return nameImageMapping.get(name);}
    static {
        ArrayMap<String, Integer> map = new ArrayMap<>();
        map.put(HEADWEAR_BEANIE, R.drawable.beanie);
        map.put(HEADWEAR_HAT, R.drawable.hat);
        map.put(HEADWEAR_UMBRELLA, R.drawable.umbrella);
        map.put(TOP_SNOWJACKET, R.drawable.jacket);
        map.put(TOP_RAINJACKET, R.drawable.jacket);
        map.put(TOP_SWEATSHIRT, R.drawable.sweatshirt);
        map.put(TOP_LONGSLEEVE, R.drawable.sweatshirt);
        map.put(TOP_TSHIRT, R.drawable.shirt);
        map.put(TOP_TANKTOP, R.drawable.shirt);
        map.put(BOTTOM_PANTS, R.drawable.pants);
        map.put(BOTTOM_SHORTS, R.drawable.shorts);
        map.put(BOTTOM_SNOWPANTS, R.drawable.pants);

        nameImageMapping = map;
    }

    /**
     * public static final String COFFEE = "coffee";
     public static final String FITNESS = "fitness";
     public static final String RESTAURANT = "restaurant";
     public static final String MOVIES = "movies";
     public static final String HIKING = "hiking";
     public static final String BOWLING = "bowling";
     public static final String READING = "reading";
     public static final String NIGHTCLUB = "nightclub";
     public static final String SHOPPING = "shopping";

     */
    private static ArrayMap<String, String[]> preferenceSearchMap;
    static {
        preferenceSearchMap = new ArrayMap<>();
        preferenceSearchMap.put(Preferences.COFFEE, new String[]{"cafe"});
        preferenceSearchMap.put(Preferences.FITNESS, new String[]{"gym"});
        preferenceSearchMap.put(Preferences.RESTAURANT, new String[]{"restaurant", "bakery"});
        preferenceSearchMap.put(Preferences.MOVIES, new String[]{"movie_theater, movie_rental"});
        preferenceSearchMap.put(Preferences.HIKING, new String[]{"park"});
        preferenceSearchMap.put(Preferences.BOWLING, new String[]{"bowling_alley"});
        preferenceSearchMap.put(Preferences.READING, new String[]{"library"});
        preferenceSearchMap.put(Preferences.NIGHTCLUB, new String[]{"night_club"});
        preferenceSearchMap.put(Preferences.SHOPPING, new String[]{"shopping_mall"});
    }

    private PreferencesManager preferencesManager;
    private Context preferencesContext;

    // Suggestions without clothing, separated by category
    private ArrayList<ArrayList<Suggestion>> suggestionsByCategory;
    public ArrayList<ArrayList<Suggestion>> getSuggestionsByCategory() {return suggestionsByCategory;}

    // This array corresponds directly to the one above
    private ArrayList<String> keysInOrder;
    public ArrayList<String> getKeysInOrder() {return keysInOrder;}

    // ArrayList of JUST clothing suggestions
    private ArrayList<Suggestion> clothingSuggestions;
    public ArrayList<Suggestion> getClothingSuggestions() {return clothingSuggestions;}

    // All suggestions mixed together, including clothing
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
        // Build out suggestions list will all suggestions grouped together
        // And one that keeps them organized by category
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        ArrayList<ArrayList<Suggestion>> categories = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();

        // Retrieve and add clothing suggestions
        ArrayList<Suggestion> newClothingSuggestions = retrieveClothingSuggestions(lastWeatherRetrieved.getMain().getTemp(), lastWeatherRetrieved.isRainingOrSnowing());
        this.clothingSuggestions = newClothingSuggestions;
        suggestions.addAll(clothingSuggestions);

        // Parse through fetch results and generate new suggestions
        for (String searchTerm : fetchResults.keySet()) {

            PlacesResult placesResult = fetchResults.get(searchTerm);
            if (placesResult.getResults().isEmpty()) continue;

            keys.add(searchTerm);

            ArrayList<Suggestion> newCategory = new ArrayList<>();
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
                newCategory.add(newSuggestion);
            }
            //Add category of suggestions to list
            categories.add(newCategory);
        }

        // Set new suggestions and alert all listeners of change
        mSuggestions = suggestions;
        suggestionsByCategory = categories;
        keysInOrder = keys;
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


    // Below 50, suggest indoor activities
    static private String[] below50Activities = {Preferences.BOWLING, Preferences.MOVIES, Preferences.READING};

    // If its a nice day out, suggest social activities
    static private String[] below80Activities = {Preferences.FITNESS, Preferences.COFFEE, Preferences.NIGHTCLUB, Preferences.RESTAURANT};

    // Its warm out, suggest some active options
    static private String[] above80Activities = {Preferences.SHOPPING, Preferences.HIKING, Preferences.FITNESS};

    // Ignore these activities if the weather is rough
    static private String[] poorWeatherAvoidActivities = {Preferences.HIKING, Preferences.NIGHTCLUB, Preferences.SHOPPING};

    private ArrayList<String> getSearchTerms(double avgTemp, boolean rainOrSnow) {
        ArrayList<String> prefList = preferencesManager.getPrefList(preferencesContext);
        ArrayList<String> searchTerms = new ArrayList<>();

        // Compare user's preferences to suggestions
        String[] suggestedActivities;
        if (avgTemp < 50) {
            suggestedActivities = below50Activities;
        }
        else if (avgTemp < 80) {
            suggestedActivities = below80Activities;
        }
        else {
            suggestedActivities = above80Activities;
        }

        // Copy values to an arraylist
        // Filter out options related to rain or snow if necessary
        ArrayList<String> filteredPrefs = new ArrayList<>();
        List<String> poorWeatherList = Arrays.asList(poorWeatherAvoidActivities);

        for (String suggestedActivity : suggestedActivities) {
            if (rainOrSnow && !poorWeatherList.contains(suggestedActivity)) {
                filteredPrefs.add(suggestedActivity);
            }
            else {
                filteredPrefs.add(suggestedActivity);
            }
        }

        // Now actually add search terms
        String[] terms;
        for (String filteredPref : filteredPrefs) {
            terms = preferenceSearchMap.get(filteredPref);
            for (String term : terms) {
                searchTerms.add(term);
            }
        }

        return searchTerms;
    }

    private ArrayList<Suggestion> retrieveClothingSuggestions(double avgTemp, boolean rainOrSnow) {
        ArrayList<Suggestion> suggestions = new ArrayList<>();
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
        return suggestions;
    }

    public interface SuggestionListener {
        void newDataFetched();
    }

}
