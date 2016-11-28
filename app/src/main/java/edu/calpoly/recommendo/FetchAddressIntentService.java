package edu.calpoly.recommendo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.calpoly.recommendo.R;

/**
 * Created by sethbarrios on 11/27/16.
 * Code based on article at https://developer.android.com/training/location/display-address.html
 */

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddrService";

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "edu.calpoly.recommendo";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        final ResultReceiver receiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> mAddresses = null;

        // Try to fetch location
        // Return error message if fails
        try {
            mAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException ioException) {
            errorMessage = getString(R.string.network_error_service_unavailable);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalException) {
            errorMessage = getString(R.string.invalid_location);
            Log.e(TAG, errorMessage, illegalException);
        }


        Bundle bundle = new Bundle();
        // Decide whether failure or success is returned to creator
        if (mAddresses == null || mAddresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            bundle.putString(Constants.RESULT_DATA_KEY, errorMessage);
            receiver.send(Constants.FAILURE_RESULT, bundle);
        }
        else {
            bundle.putString(Constants.RESULT_DATA_KEY, mAddresses.get(0).getLocality());
            receiver.send(Constants.SUCCESS_RESULT, bundle);
        }
    }
}
