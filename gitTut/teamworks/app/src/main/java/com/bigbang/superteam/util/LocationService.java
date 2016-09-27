package com.bigbang.superteam.util;

/**
 * Created by USER 8 on 14-Jul-16.
 */

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by momo on 11.06.2015.
 */
public class LocationService extends Service
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("", "********************** onStartCommand()");
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("", "********************** onBind()");
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("", "********************** onConnected()");
//        Util.appendLog("Location Service onConnected()");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

            Util.WriteSharePrefrence(getApplicationContext(), "track_lat", "" + lat);
            Util.WriteSharePrefrence(getApplicationContext(), "track_lng", "" + lon);

            Util.appendLog("Location Service onConnected(): " + " Latitude: " + lat + " Longitude: " + lon);
        }
    }

    @Override
    public void onCreate() {
        int permissionCheck = ContextCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            buildGoogleApiClient();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Util.appendLog("Location Service onConnectionSuspended()");
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("", "********************** onLocationChanged()");
//        Util.appendLog("Location updated Latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
//        Toast.makeText(LocationService.this, "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        if (location != null) {
            Util.WriteSharePrefrence(getApplicationContext(), "track_lat", "" + location.getLatitude());
            Util.WriteSharePrefrence(getApplicationContext(), "track_lng", "" + location.getLongitude());
        }
//        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Util.appendLog("Location Service onConnectionFailed()");
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.appendLog("Location servcie destroyed()");
    }
}
