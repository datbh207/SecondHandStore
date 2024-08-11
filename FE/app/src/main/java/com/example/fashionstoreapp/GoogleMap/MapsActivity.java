package com.example.fashionstoreapp.GoogleMap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.example.fashionstoreapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RouteListener {

    private GoogleMap map;
    private Spinner spinner;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng shipperLocation, destLocation;
    private ProgressDialog dialog;
    private RouteManager routeManager;
    private String addressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        // GetData
        Intent intent = getIntent();
        addressData =intent.getStringExtra("address");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);



        dialog = new ProgressDialog(MapsActivity.this);
        routeManager = new RouteManager(this, this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void addSpinnerOptions() {
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> listStyleMap = new ArrayList<String>();
        listStyleMap.add("Normal");
        listStyleMap.add("Terrain");
        listStyleMap.add("Satellite");
        listStyleMap.add("Hybrid");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listStyleMap);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case 2:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 3:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    default:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupUIMap(){
        // Mode of Map
        addSpinnerOptions();

        // Setting Zoom In/Out
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
    }

    private void addMarker(LatLng latLng, int iconRes, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(IconHelper.setIcon(this, iconRes))
                .title(title);
        map.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Show Map
        map = googleMap;
        setupUIMap();

        // setshipperLocation
        shipperLocation = new LatLng(10.875358833414383, 106.80088062970046);
        addMarker(shipperLocation, R.drawable.ic_person_pin_red, "Your Location");

        // Zoom Camera to "Your Location"
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(shipperLocation)
                .zoom(12)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        // SetUserLocation
        destLocation = getLatLng(addressData);
        addMarker(destLocation, R.drawable.ic_person_pin_green, addressData);
        routeManager.drawRoute(shipperLocation, destLocation);
    }

    private LatLng getLatLng(String addressData) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng latLng = null;
        try {
            addresses = geocoder.getFromLocationName(addressData, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    @Override
    public void onRouteFailure(ErrorHandling e) {
        Toast.makeText(MapsActivity.this, "Route Failed", Toast.LENGTH_SHORT).show();
        Log.w("TAG", "onRoutingFailure: " + e);
    }

    @Override
    public void onRouteStart() {
        Toast.makeText(MapsActivity.this, "Route Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> list, int indexing) {
        Toast.makeText(MapsActivity.this, "Route Success", Toast.LENGTH_SHORT).show();
        dialog.setMessage("Route is generating, please wait");
        dialog.show();

        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == indexing) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing" + indexing);
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(12);
                polylineOptions.addAll(list.get(indexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = map.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }

        dialog.dismiss();
    }

    @Override
    public void onRouteCancelled() {
        Toast.makeText(MapsActivity.this, "Route Cancelled", Toast.LENGTH_SHORT).show();
    }
}