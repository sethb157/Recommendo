package edu.calpoly.recommendo.activities;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import edu.calpoly.recommendo.R;
import edu.calpoly.recommendo.managers.suggestions.Suggestion;
import edu.calpoly.recommendo.managers.suggestions.SuggestionsManager;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Dan on 11/27/2016.
 */

public class MapActivity extends Activity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private Location mLocation;
    private ArrayList<Suggestion> mSuggestions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, this);

            MapFragment mapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container_map, mapFragment);
            fragmentTransaction.commit();

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);

            if (mLocation != null) {
                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
            }
        }

        mSuggestions = SuggestionsManager.getSuggestions();
        if (mSuggestions != null) {
            for (Suggestion item : mSuggestions) {
                if (item.getType() != null && item.getType().equals(SuggestionsManager.TYPE_ACTIVITY)) {
                    float color;
                    switch (item.getCategory()) {
                        case "movie_rental":
                            color = BitmapDescriptorFactory.HUE_AZURE;
                            break;
                        case "movie_theater":
                            color = BitmapDescriptorFactory.HUE_BLUE;
                            break;
                        case "cafe":
                            color = BitmapDescriptorFactory.HUE_ORANGE;
                            break;
                        case "gym":
                            color = BitmapDescriptorFactory.HUE_YELLOW;
                            break;
                        case "restaurant":
                            color = BitmapDescriptorFactory.HUE_RED;
                            break;
                        case "park":
                            color = BitmapDescriptorFactory.HUE_GREEN;
                            break;
                        default:
                            color = BitmapDescriptorFactory.HUE_VIOLET;
                    }
                    Log.d(TAG, "onMapReady: " + item.getCategory());
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(item.getLatitude(), item.getLongitude()))
                            .title(item.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(color));
                    mMap.addMarker(marker);
                }
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                int key = 0;
                if (mSuggestions != null) {
                    for (Suggestion item : mSuggestions) {
                        if (item.getName() != null && item.getName().equals(title)) {
                            Intent myIntent = new Intent(getApplicationContext(), DetailSuggestionActivity.class);
                            myIntent.putExtra("key", key); //Optional parameters
                            startActivity(myIntent);
                            break;
                        }
                        key++;
                    }
                }

            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
        }

    }

   @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
