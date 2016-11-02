package edu.uwm.ibidder.Location;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.support.v4.app.ActivityCompat;



public abstract class LocationService implements LocationListener {
    //Location Services client variable
    GoogleApiClient locGoogleApiClient = null;
    Location mLastLocation = null;


    private double latitude;
    private double longitude;

    LocationManager lm = null;

    public LocationService(Context context) {
        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 10F, (LocationListener) this);
    }

    public abstract void getCoordinates(double lat, double longi);

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        getCoordinates(latitude,longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b){

    }

    @Override
    public void onProviderEnabled(String s){

    }

    @Override
    public void onProviderDisabled(String s){

    }
}
