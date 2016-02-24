package com.melascuco.android.coordinatessender;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmReciever extends BroadcastReceiver implements LocationListener {

    public static final String TAG = AlarmReciever.class.getCanonicalName();

    public static final long LOCATION_CHECK_INTERVAL  = 60000; // 1 MIN
    public static final long LOCATION_SEARCH_DURATION = 20000;  // 20 SEG
    public static final long ACCURACY_THRESHOLD = 35;  // 35 ms


    //TODO how to stop sending gps data

    private Context appContext;

    private LocationManager locationManager;
    //private ArrayList<Location> locationHistory;
    private Location lastBestLocation;

    private final Handler stopGpsHandler = new Handler();

    private final Runnable stopGpsSearch = new Runnable() {
        @Override
        public void run() {
            if (lastBestLocation == null) {
                lastBestLocation = LocationUtils.findMostAccurateLastKnownLocation(appContext);
            }
            //TODO addAndSendLocations(lastBestLocation,....);
            locationManager.removeUpdates(AlarmReciever.this);
            Log.d(TAG, "Location updates removed!");
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm Triggered, getting location!", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Starting location search...");

        appContext = context.getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        stopGpsHandler.postDelayed(stopGpsSearch, LOCATION_SEARCH_DURATION);

//        Location location = LocationUtils.findMostAccurateLastKnownLocation(context);
//        Log.d(TAG, "Location obtained: " + location.getProvider() + ", "
//                + location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed: " + location.getProvider() + ", "
                + location.getLatitude() + "," + location.getLongitude());

        if (lastBestLocation == null) {
            lastBestLocation = location;
        } else {
            if (lastBestLocation.getAccuracy() > location.getAccuracy()) {
                lastBestLocation = location;
            }
        }

        if (location.getAccuracy() < ACCURACY_THRESHOLD) {
            stopGpsHandler.removeCallbacks(stopGpsSearch);
            locationManager.removeUpdates(AlarmReciever.this);
            //TODO addAndSendLocations(lastBestLocation,....);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}