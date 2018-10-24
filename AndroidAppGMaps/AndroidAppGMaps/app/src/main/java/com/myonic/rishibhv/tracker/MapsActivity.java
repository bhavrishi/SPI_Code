package com.myonic.rishibhv.tracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements LocationListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    protected static LocationManager locationManager;
    /* GPSTracker*/
    int i = 0;
    ArrayList<LatLng> markerPoints;
    boolean isGPSEnabled = false;
    LatLng origin;
    /* flag for network status*/
    boolean isNetworkEnabled = false;
    /* flag for GPS status*/
    boolean canGetLocation = false;
    GoogleMap mMap;
    /*   location*/
    Location location, openLoc = null;
    /*     latitude & longitude*/
    double latitude;
    double longitude;
    LatLng destFinal;

    /*check to ensure if the user enabled GPS, else navigate to Setting->Location interface*/
    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            /*  Should we show an explanation?*/
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

/*                 Show an explanation to the user *asynchronously* -- don't block
                 this thread waiting for the user's response! After the user
                 sees the explanation, try again to request the permission.*/
                new AlertDialog.Builder(this)
                        .setTitle("Request")
                        .setMessage("Enable location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MapConstants.MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {

                /* No explanation needed, we can request the permission.*/
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MapConstants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MapConstants.MY_PERMISSIONS_REQUEST_LOCATION: {
                /*                 If request is cancelled, the result arrays are empty.*/
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

/*                     permission was granted, yay! Do the
                     location-related task you need to do.*/
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MapConstants.MIN_TIME_BW_UPDATES,
                                MapConstants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }


                    }

                } else {

                    new AlertDialog.Builder(this)
                            .setTitle("Location Turned Off")
                            .setMessage("Please enable location to continue")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    /*                                    Prompt the user once explanation has been shown*/
                                    ActivityCompat.requestPermissions(MapsActivity.this,
                                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            MapConstants.MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();

                }
                return;
            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        markerPoints = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        /*getting GPS status*/
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onLocationChanged(Location loc) {
        FetchDataAsyncTask fetchUrl = null;
        if ((i >= 1) && (loc.getLatitude() != openLoc.getLatitude()) && (loc.getLongitude() != openLoc.getLongitude())) {
            LatLng dest = new LatLng(loc.getLatitude(), loc.getLongitude());
            String url = getUrl(origin, dest);
            fetchUrl = new FetchDataAsyncTask(MapsActivity.this);
            try {
                //my favourite part starts here
                fetchUrl.execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);
            /* getting GPS status*/
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            /* getting network status*/
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                /* no network provider is enabled*/
                checkLocationPermission();
            } else {
                this.canGetLocation = true;
                /* First get location from Network Provider*/
                if (isGPSEnabled) {
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MapConstants.MIN_TIME_BW_UPDATES,
                            0, this);

                }
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        checkLocationPermission();
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MapConstants.MIN_TIME_BW_UPDATES,
                            0, this);
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                /* if GPS Enabled get lat/long using GPS Services*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        getLocation();
        double lat, lang;
        if (canGetLocation()) {
            if (i < 0) {
                lat = getLatitude();
                lang = getLongitude();
                latLng = new LatLng(lat, lang);
                locationManager.removeUpdates(this);
                markerPoints.add(destFinal);
                MarkerOptions options2 = new MarkerOptions()
                        .position(destFinal)
                        .title("I am finally here now!");
                mMap.addMarker(options2);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(3));
                Toast.makeText(this, "Tracking Completed", Toast.LENGTH_SHORT).show();
            }
        } else {
/*           can't get location
             GPS or Network is not enabled
             Ask user to enable GPS/network in settings*/
            showSettingsAlert();
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        /*Origin of route*/
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        /*Destination of route*/
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        /*Sensor enabled*/
        String sensor = "sensor=false";

        /* Building the parameters to the web service*/
        String parameters = str_origin + "&" + str_dest + "&" + sensor+"&"+"&key=YOUR_API_KEY";

        /*Output format*/
        String output = "json";

        /* Building the url to the web service*/
        String url = MapConstants.REST_API + output + "?" + parameters;
        return url;
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        /*Setting Dialog Title*/
        alertDialog.setTitle("GPS is settings");

        /*Setting Dialog Message*/
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        /*On pressing Settings button*/
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        /*on pressing cancel button*/
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        /*Showing Alert Message*/
        alertDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        /* return latitude*/
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        /*return longitude*/
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
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