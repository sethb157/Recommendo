package edu.calpoly.recommendo.managers.places;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;

import edu.calpoly.recommendo.managers.places.scheme.PlacesResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sethbarrios on 11/24/16.
 */

public class PlacesFetcher {

    /* Retrofit setup*/
    private static final String TAG = "PlacesFetcher";
    private Retrofit retrofit;
    private PlacesAPI placesService;
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private final static String PLACES_API_KEY = "AIzaSyAWIWJ9WuWlQ2hHIlJgqCLBRXpmB3pMY2Y";

    /* Values for fetch*/
    public ArrayList<String> placeTypes = new ArrayList<>();
    // Default values for Tokyo, Japan. Don't forget to set these
    public String latitude = "35.6895", longitude = "139.6917";
    public int radius = 1600;

    /* For results and monitoring calls*/
    public PlacesFetcherListener listener;
    private ArrayMap<String, PlacesResult> fetchResultsMap = new ArrayMap<>();
    private ArrayList<Call> enqueuedCalls = new ArrayList<>();


    // Setup retrofit and new service
    public PlacesFetcher() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placesService = retrofit.create(PlacesAPI.class);
    }

    // Setters and getters
    public ArrayMap<String, PlacesResult> getLatestResults() {return fetchResultsMap;}

    /**
     * Asyncronous call to retrieve results for one place type
     *
     */
    public void fetchPlaces() {

        // Get rid of old results
        fetchResultsMap.clear();

        // Fetch all place types
        for (final String placeType : placeTypes) {
            Call<PlacesResult> call = placesService.getPlaces(latitude + "," + longitude,
                    radius, placeType, PLACES_API_KEY);
            enqueuedCalls.add(call);
            call.enqueue(new Callback<PlacesResult>() {
                @Override
                public void onResponse(Call<PlacesResult> call, Response<PlacesResult> response) {
                    fetchResultsMap.put(placeType, response.body());
                    Log.d(TAG, "onResponse: " + placeType + " finishedFetching");
                    callFinished(call);

                }

                @Override
                public void onFailure(Call<PlacesResult> call, Throwable t) {
                    // Remove failed call from queue
                    callFinished(call);
                    Log.d(TAG, "onFailure: " + placeType + "failedFetching");
                }
            });
        }
    }

    /**
     * Call this to determine if listener should be updated
     */
    private void callFinished(Call call) {
        enqueuedCalls.remove(call);
        if (enqueuedCalls.size() == 0 && listenerPresent()) {
            listener.placesFetchFinished(this, fetchResultsMap);
        }
    }

    /**
     * Checks if a listener is attached and warns if not
     */
    private boolean listenerPresent() {
        if (listener != null) return true;
        else {
            Log.w(TAG, "No listener attached. Events will be ignored.", null);
            return false;
        }
    }

    /**
     * Callback methods for completion of asynchronous call to places api
     */
    public interface PlacesFetcherListener {
        void placesFetchFinished(PlacesFetcher fetcher, ArrayMap<String, PlacesResult> fetchResults);
    }
}

/**
 * For use by Retrofit
 */
interface PlacesAPI {
    @GET("nearbysearch/json")
    Call<PlacesResult> getPlaces(@Query("location") String latLon,
                                      @Query("radius") int radius,
                                      @Query("type") String placeType,
                                      @Query("key") String apiKey);
}
