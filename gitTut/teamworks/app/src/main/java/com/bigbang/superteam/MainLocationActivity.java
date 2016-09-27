package com.bigbang.superteam;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.bigbang.superteam.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainLocationActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

//             Getting LocationManager object from System Service LOCATION_SERVICE
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//            // Creating a criteria object to retrieve provider
//            Criteria criteria = new Criteria();
//
//            // Getting the name of the best provider
//            String provider = locationManager.getBestProvider(criteria, true);
//
//            // Getting Current Location
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            if(location!=null){
//                onLocationChanged(location);
//            }
//            locationManager.requestLocationUpdates(provider, 20000, 0, this);

            int permissionCheck = ContextCompat.checkSelfPermission(MainLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
                buildGoogleApiClient();
            }
        }
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

            String latitude = Util.ReadSharePrefrence(MainLocationActivity.this, "track_lat");
            String longitude = Util.ReadSharePrefrence(MainLocationActivity.this, "track_lng");

            updateGoogleMap(Double.parseDouble(latitude), Double.parseDouble(longitude));
            Util.appendLog("Location Service onConnected(): " + " Latitude: " + lat + " Longitude: " + lon);
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

//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Util.appendLog("Location Service onConnectionFailed()");
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {


        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

//        updateGoogleMap(latitude, longitude);

        if (location != null) {
            Util.WriteSharePrefrence(getApplicationContext(), "track_lat", "" + location.getLatitude());
            Util.WriteSharePrefrence(getApplicationContext(), "track_lng", "" + location.getLongitude());
        }
        String latitudes = Util.ReadSharePrefrence(MainLocationActivity.this, "track_lat");
        String longitudes = Util.ReadSharePrefrence(MainLocationActivity.this, "track_lng");
        updateGoogleMap(Double.parseDouble(latitudes), Double.parseDouble(longitudes));
//        stopSelf();

    }

    private void updateGoogleMap(Double latitude, Double longitude) {
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        // Setting latitude and longitude in the TextView tv_location
        tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address, menu);
        return true;
    }
}