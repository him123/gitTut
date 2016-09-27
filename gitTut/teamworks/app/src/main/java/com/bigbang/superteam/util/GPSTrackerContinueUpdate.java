package com.bigbang.superteam.util;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTrackerContinueUpdate extends IntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GPSTrackerContinueUpdate.class.getSimpleName();
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static int DISPLACEMENT = 10;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    public GPSTrackerContinueUpdate() {
        super("GPSTrackerContinueUpdate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, " ***** Service on handled");
        Util.appendLog("***** Service on handled");
        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Util.appendLog("***** Service on connected");
        Log.e(TAG, " ***** Service on connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Util.appendLog("***** Service on suspended");
        Log.e(TAG, " ***** Service on suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location changed");
        mLastLocation = location;

        String latitude = String.valueOf(mLastLocation.getLatitude());
        String longitude = String.valueOf(mLastLocation.getLongitude());


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Util.appendLog("Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onDestroy() {
        Util.appendLog("Service is Destroying ");
        Log.e(TAG, "Service is Destroying...");
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "Location update stoping...");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "Location update starting...");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }


    protected void createLocationRequest() {
        Log.e(TAG, " ***** Creating location request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            Util.appendLog("Play service available");
            return true;
        } else {
            Log.e(TAG, " ***** Update google play service ");
            Util.appendLog("***** Please Update google play service ");
            return false;
        }
    }
}