package edu.calpoly.recommendo.places;

import java.util.Objects;

import edu.calpoly.recommendo.activities.Preferences;
import edu.calpoly.recommendo.places.scheme.PlacesJSON;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Dan on 11/24/2016.
 */

public class PlacesManager {
    public static final String appID = "AIzaSyAIn2OxrUIZCh3KESnoNYRnsfXUY6QmsNo";
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private Retrofit retrofit;
    private String TAG = "PlacesManager";

    // Creates a new retrofit instance
    public PlacesManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * @param delegate
     *  the object that gets notified when async call completes
     */
    public void getPlaces(final PlacesManager.PlacesManagerDelegate delegate,
                                         String latitude, String longitude, String type) {
        PlacesAPI placesService = retrofit.create(PlacesAPI.class);
        String location = latitude + "," + longitude;
        Call<PlacesJSON> placesAPICall;
        if (Objects.equals(type, Preferences.BIKING))
            placesAPICall = placesService.getBikingInfo(location);
        else if (Objects.equals(type, Preferences.COFFEE))
            placesAPICall = placesService.getCoffeeInfo(location);
        else if (Objects.equals(type, Preferences.FITNESS))
            placesAPICall = placesService.getFitnessInfo(location);
        else if (Objects.equals(type, Preferences.GOLF))
            placesAPICall = placesService.getGolfInfo(location);
        else if (Objects.equals(type, Preferences.HIKING))
            placesAPICall = placesService.getHikingInfo(location);
        else if (Objects.equals(type, Preferences.MOVIES))
            placesAPICall = placesService.getMoviesInfo(location);
        else if (Objects.equals(type, Preferences.RESTAURANT))
            placesAPICall = placesService.getRestaurantInfo(location);
        else if (Objects.equals(type, Preferences.PIZZA))
            placesAPICall = placesService.getPizzaInfo(location);
        else if (Objects.equals(type, Preferences.RUNNING))
            placesAPICall = placesService.getRunningInfo(location);
        else if (Objects.equals(type, Preferences.SWIMMING))
            placesAPICall = placesService.getSwimmingInfo(location);
        else
            placesAPICall = placesService.getPlacesInfo(location);

        placesAPICall.enqueue(new Callback<PlacesJSON>() {

            // When response returns, let the caller know of the result
            @Override
            public void onResponse(Call<PlacesJSON> call, Response<PlacesJSON> response) {
                if (delegate != null) delegate.placesFetchSucceeded(response.body());
            }
            @Override
            public void onFailure(Call<PlacesJSON> call, Throwable t) {
                if (delegate != null) delegate.placesFetchFailed();
            }
        });
    }

    // This interface reports back how fetching new weather data went
    public interface PlacesManagerDelegate {
        void placesFetchFailed();
        void placesFetchSucceeded(PlacesJSON response);
    }
}

// For interacting with RetroFit
interface PlacesAPI {
    @GET("?key=" + PlacesManager.appID + "&rankby=distance")
    Call<PlacesJSON> getPlacesInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=park&radius=5000")
    Call<PlacesJSON> getBikingInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=cafe&rankby=distance&opennow=true")
    Call<PlacesJSON> getCoffeeInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=gym&rankby=distance&opennow=true")
    Call<PlacesJSON> getFitnessInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&keyword=golf&rankby=distance&opennow=true")
    Call<PlacesJSON> getGolfInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&keyword=hiking area&rankby=distance")
    Call<PlacesJSON> getHikingInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=movie_theater&rankby=distance&opennow=true")
    Call<PlacesJSON> getMoviesInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=restaurant&rankby=distance&opennow=true")
    Call<PlacesJSON> getRestaurantInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&type=meal_delivery&rankby=distance&opennow=true")
    Call<PlacesJSON> getPizzaInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&radius=2500")
    Call<PlacesJSON> getRunningInfo(@Query("location") String location);

    @GET("?key=" + PlacesManager.appID + "&keyword=swimming pool&rankby=distance&opennow=true")
    Call<PlacesJSON> getSwimmingInfo(@Query("location") String location);
}

