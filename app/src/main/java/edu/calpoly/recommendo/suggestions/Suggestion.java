package edu.calpoly.recommendo.suggestions;

/**
 * Created by Dan on 11/22/2016.
 */
public class Suggestion {

    private String mName;
    private String mAddress;
    private String mUrl;
    private String mType;
    private String mCategory;
    private String mPicRef;
    private double mLatitude;
    private double mLongitude;

    public Suggestion(String name, String address, String url, String type, String category, String picRef, double latitude, double longitude) {
        mName = name;
        mAddress = address;
        mUrl = url;
        mType = type;
        mCategory = category;
        mLatitude = latitude;
        mLongitude = longitude;
        mPicRef = picRef;
    }

    public Suggestion(String name, String address, String url, String type, String category) {
        mName = name;
        mAddress = address;
        mUrl = url;
        mType = type;
        mCategory = category;
        mLatitude = 0;
        mLongitude = 0;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getType() {
        return mType;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getPicRef() {return mPicRef;}

    public Double getLatitude() {return mLatitude;}

    public Double getLongitude() {return mLongitude;}
}
