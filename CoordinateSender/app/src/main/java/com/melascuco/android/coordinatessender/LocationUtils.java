package com.melascuco.android.coordinatessender;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.List;

public class LocationUtils {

    public static final long OK_LOCATION_AGE = 0;
    public static final String TAG = LocationUtils.class.getCanonicalName();

    public static Location findMostAccurateLastKnownLocation(Context context) {
        Log.d(TAG, "Location find...");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = 0;
        long minTime = System.currentTimeMillis() - OK_LOCATION_AGE;
        Location bestLocation = null;

        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider : matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.d(TAG, "Location not null!");
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    Log.d(TAG, "Location updated");
                    bestLocation = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                } else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                    Log.d(TAG, "Location updated, first value");
                    //When there is not previous location
                    bestLocation = location;
                    bestTime = time;
                }
            }
        }

        Log.d(TAG, "New best location: " + bestLocation.getProvider() + ", "
                + bestLocation.getLatitude() + "," + bestLocation.getLongitude());
        return bestLocation;
    }
}
