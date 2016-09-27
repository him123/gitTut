package com.bigbang.superteam.workitem;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by USER 3 on 16/05/2015.
 */
public class SelectLocationActivity extends Activity {
    Button doneBtn;
    EditText etLocation;
    GoogleMap map;
    GPSTracker gps;
    String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlocation);
        Init();
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Double lat = 0.0;
            Double lng = 0.0;
            if (gps.getLatitude() != 0)
                lat = gps.getLatitude();
            if (gps.getLongitude() != 0)
                lng = gps.getLongitude();
            initMapOnCurrentLocation(lat, lng);
        } else {
            gps.showSettingsAlert();
        }
    }

    private void Init() {
        etLocation = (EditText) findViewById(R.id.et_location);
        doneBtn = (Button) findViewById(R.id.btn_done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultData = new Intent();

                resultData.putExtra(WorkItem.WORK_LONGITUDE, "" + lng);
                resultData.putExtra(WorkItem.WORK_LATITUDE, "" + lat);
                resultData.putExtra(WorkItem.WORK_LOCATION, "" + etLocation.getText().toString());

                setResult(Activity.RESULT_OK, resultData);
                finish();
            }
        });

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(SelectLocationActivity.this, Locale.getDefault());
                lat = "" + latLng.latitude;
                lng = "" + latLng.longitude;
                etLocation.setText(
                        Util.getAddress(getApplicationContext(), latLng.latitude, latLng.longitude));
            }
        });
    }

    private void initMapOnCurrentLocation(double latitude, double longitude) {
        if (map != null) {

            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            LatLng latlng = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng) // Sets the center of the map to Mountain
                            // View
                    .zoom(15) // Sets the zoom
                    .bearing(90) // Sets the orientation of the camera to east
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }
    }
}
