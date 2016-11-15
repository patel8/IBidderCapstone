package edu.uwm.ibidder.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


public abstract class LocationService implements LocationListener {
    private Location lastLocation;
    private boolean waitingForLocation;

    private LocationManager locationManager;
    private Context context;

    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        waitingForLocation = false;
        lastLocation = null;

        rebuild();
    }

    public void updateLocation() {
        if (lastLocation == null)
            waitingForLocation = true;
        else
            getCoordinates(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public void rebuild() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, this, null);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, this);
    }

    public void dispose() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    public abstract void getCoordinates(double lat, double longi);

    @Override
    public void onLocationChanged(Location location) {
        if (waitingForLocation) {
            getCoordinates(location.getLatitude(), location.getLongitude());
            waitingForLocation = false;
        }

        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
