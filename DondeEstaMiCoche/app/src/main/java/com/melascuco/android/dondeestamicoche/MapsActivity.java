package com.melascuco.android.dondeestamicoche;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final String PREF_LOCATION_LATITUDE = "PREF_LOCATION_LATITUDE";
    public static final String PREF_LOCATION_LONGITUDE = "PREF_LOCATION_LONGITUDE";
    public static final double PREF_LOCATION_DEFAULT = 0.0;
    public static final String PREF_DATESAVED = "PREF_DATESAVED";
    public static final long PREF_DATESAVED_DEFAULT = 0L;

    public static final int MAP_ZOOM = 15;

    private GoogleMap mMap;
    private SharedPreferences sharedPref;
    private MarkerOptions markerCar;

    private double lastSavedLatitude;
    private double lastSavedLongitude;
    private Date lastSavedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "MapsActivity onCreate");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "Map async obtained");

        markerCar = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
        //markerMyself = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.rabbit));

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        lastSavedLatitude = getDouble(sharedPref, PREF_LOCATION_LATITUDE, PREF_LOCATION_DEFAULT);
        lastSavedLongitude = getDouble(sharedPref, PREF_LOCATION_LONGITUDE, PREF_LOCATION_DEFAULT);
        lastSavedDate = new Date(sharedPref.getLong(PREF_DATESAVED, PREF_DATESAVED_DEFAULT));
        Log.d(TAG, "Saved preferences: " + lastSavedLatitude + "," + lastSavedLongitude
                + " y " + DateFormat.getDateTimeInstance().format(lastSavedDate));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "My location button!");
                return !checkLocationServicesEnabled();
            }
        });

        TextView textView = (TextView) findViewById(R.id.textView);

        if (lastSavedLatitude==PREF_LOCATION_DEFAULT && lastSavedLongitude==PREF_LOCATION_DEFAULT) {
            Log.d(TAG, "Location has never been saved");
            textView.setText("Aún no hay lugares almacenados donde he aparcado.");
        } else {
            Log.d(TAG, "Location was saved and retrieved");
            textView.setText("Aparqué aquí el " + DateFormat.getDateTimeInstance().format(lastSavedDate));
            LatLng lastSavedParkingLocation = new LatLng(lastSavedLatitude, lastSavedLongitude);
            mMap.clear();
            mMap.addMarker(markerCar.position(lastSavedParkingLocation).anchor(0.5f, 0.5f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSavedParkingLocation));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(MAP_ZOOM);
            mMap.animateCamera(zoom);
        }
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public void onButtonAparcadoClick(View w) {
        Log.d(TAG, "onButtonAparcadoClick launched");
        if (checkLocationServicesEnabled()) {
            Log.d(TAG, "Location services are enabled");
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Salvar aparcamiento");
            builder.setMessage("¿Quiero guardar como aparcamiento mi posición en el mapa?");
            builder.setPositiveButton("Vale", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Guardar aparcamiento: SI");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    Location bestLastLocation = findCurrentLocation();
                    putDouble(editor, PREF_LOCATION_LATITUDE, bestLastLocation.getLatitude());
                    putDouble(editor, PREF_LOCATION_LONGITUDE, bestLastLocation.getLongitude());
                    editor.putLong(PREF_DATESAVED, new Date().getTime());
                    editor.commit();
                    Log.d(TAG, "Last location "
                            + bestLastLocation.getLatitude() + "," + bestLastLocation.getLongitude()
                            + " saved in shared preferences");
                    mMap.clear();
                    mMap.addMarker(markerCar
                            .position(new LatLng(bestLastLocation.getLatitude(), bestLastLocation.getLongitude()))
                            .anchor(0.5f, 0.5f));
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Guardar aparcamiento: NO");
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void onTextAparcadoClick(View w) {
        Log.d(TAG, "onTextAparcadoClick launched");
        if (lastSavedLatitude==PREF_LOCATION_DEFAULT && lastSavedLongitude==PREF_LOCATION_DEFAULT) {
            Log.d(TAG, "Location has never been saved, nothing is done");
        } else {
            Log.d(TAG, "Location was saved and retrieved");
            LatLng lastSavedParkingLocation = new LatLng(lastSavedLatitude, lastSavedLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSavedParkingLocation));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(MAP_ZOOM);
            mMap.animateCamera(zoom);
        }
    }

    private boolean checkLocationServicesEnabled() {
        boolean locationEnabled = true;
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;

            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Activar Location");
            builder.setMessage("No tienes activados los servicios de ubicación");
            builder.setPositiveButton("Vale", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        Log.d(TAG, "Location enables returns " + locationEnabled);
        return locationEnabled;
    }

    private Location findCurrentLocation() {
        return mMap.getMyLocation();
    }

}
