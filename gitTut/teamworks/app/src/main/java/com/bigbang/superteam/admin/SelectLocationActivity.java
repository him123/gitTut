package com.bigbang.superteam.admin;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectLocationActivity extends FragmentActivity {

    GoogleMap map;
    GPSTracker gps;
    Double latitude, longitude;
    String TAG = "SelectLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        ButterKnife.inject(this);

        gps = new GPSTracker(SelectLocationActivity.this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {

            init();

            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                loadMap(latitude, longitude);
            } else {
                gps.showSettingsAlert();
            }
        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        gps = new GPSTracker(SelectLocationActivity.this);
        if (!gps.canGetLocation()) gps.showSettingsAlert();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {
        write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
        write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
        write(Constant.SHRED_PR.KEY_RELOAD, "1");
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    private void init() {

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);


        Log.e(TAG,"Inside init before setOnMapClickListener");

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
//                write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
//                write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
//                write(Constant.SHRED_PR.KEY_RELOAD, "1");
                Log.e(TAG,"Inside setOnMapClickListener");
                loadMap(latitude, longitude);
            }
        });

    }

    private void loadMap(double latitude, double longitude) {
        Log.e(TAG,"Inside loadMap");
        if (map != null) {
            map.clear();
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            Log.e(TAG,"Inside loadMap & (map != null");

            LatLng latlng = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.map_marker)));

            Log.e(TAG,"Inside loadMap & (map != null **************");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            Log.e(TAG,"Inside loadMap & (map != null ###########");
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng) // Sets the center of the map to Mountain
                            // View
                    .zoom(15) // Sets the zoom
                    .bearing(90) // Sets the orientation of the camera to east
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            map.setMyLocationEnabled(true);

            Log.e(TAG,"Inside loadMap & (map != null) after CameraPosition");

        }
    }


    protected void write(String key, String val) {
        Util.WriteSharePrefrence(this, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(this, key);
    }
}
