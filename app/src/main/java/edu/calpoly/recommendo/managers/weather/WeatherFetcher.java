package edu.calpoly.recommendo.managers.weather;

import android.util.Log;

import edu.calpoly.recommendo.managers.weather.scheme.WeatherJSON;
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

public class WeatherFetcher {
    // Retrofit setup
    public static final String appID = "1c4a575d78d5e059a929b5be90c1cd17";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private Retrofit retrofit;
    WeatherAPI weatherService;
    private String TAG = "WeatherManager";

    // Request info
    public String latitude = "35.6895", longitude = "139.6917";

    // Dataflow vars
    public WeatherFetcherListener listener;

    // Creates a new retrofit instance
    public WeatherFetcher() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherAPI.class);
    }

    /**
     * Refreshes weather for this instance
    */
    public void fetchWeather() {
        Call<WeatherJSON> weatherAPICall = weatherService.getWeatherInfo(latitude, longitude);
        weatherAPICall.enqueue(new Callback<WeatherJSON>() {

            // When response returns, let the caller know of the result
            @Override
            public void onResponse(Call<WeatherJSON> call, Response<WeatherJSON> response) {
                if (listenerPresent()) listener.weatherFetchSucceeded(response.body());
            }
            @Override
            public void onFailure(Call<WeatherJSON> call, Throwable t) {
                if (listenerPresent()) listener.weatherFetchFailed();
            }
        });
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

    // This interface reports back how fetching new weather data went
    public interface WeatherFetcherListener {
        void weatherFetchFailed();
        void weatherFetchSucceeded(WeatherJSON response);
    }
}



// For interacting with RetroFit
interface WeatherAPI {
    @GET("weather?units=imperial&appid=" + WeatherFetcher.appID)
    Call<WeatherJSON> getWeatherInfo(@Query("lat") String latitude, @Query("lon") String longitude);
}
