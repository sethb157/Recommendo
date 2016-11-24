package edu.calpoly.recommendo.weather;

import android.util.Log;

import edu.calpoly.recommendo.activities.Preferences;
import edu.calpoly.recommendo.weather.scheme.WeatherJSON;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sethbarrios on 11/14/16.
 */

public class WeatherManager {
    public static final String appID = "1c4a575d78d5e059a929b5be90c1cd17";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private Retrofit retrofit;
    private String TAG = "WeatherManager";

    // Creates a new retrofit instance
    public WeatherManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * @param delegate
     *  the object that gets notified when async call completes
    */
    public void getWeatherForCoordinates(final WeatherManagerDelegate delegate,
                                         String latitude, String longitude) {
        WeatherAPI weatherService = retrofit.create(WeatherAPI.class);
        Call<WeatherJSON> weatherAPICall = weatherService.getWeatherInfo(latitude, longitude);
        weatherAPICall.enqueue(new Callback<WeatherJSON>() {

            // When response returns, let the caller know of the result
            @Override
            public void onResponse(Call<WeatherJSON> call, Response<WeatherJSON> response) {
                if (delegate != null) delegate.weatherFetchSucceeded(response.body());
            }
            @Override
            public void onFailure(Call<WeatherJSON> call, Throwable t) {
                if (delegate != null) delegate.weatherFetchFailed();
            }
        });
    }

    // This interface reports back how fetching new weather data went
    public interface WeatherManagerDelegate {
        void weatherFetchFailed();
        void weatherFetchSucceeded(WeatherJSON response);
    }
}



// For interacting with RetroFit
interface WeatherAPI {
    @GET("weather?units=imperial&appid=" + WeatherManager.appID)
    Call<WeatherJSON> getWeatherInfo(@Query("lat") String latitude, @Query("lon") String longitude);
}
