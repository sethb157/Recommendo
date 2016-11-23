package edu.calpoly.recommendo.managers;

import android.os.Parcelable;

import java.util.ArrayList;

import edu.calpoly.recommendo.Suggestion;
import edu.calpoly.recommendo.activities.Preferences;

/**
 * Created by Dan on 11/22/2016.
 */

public class SuggestionsManager {
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

}
