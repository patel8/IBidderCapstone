package edu.uwm.ibidder.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Manages a user's location updates
 */
public abstract class LocationService implements LocationListener {
    private static final int MAX_IGNORES = 5;

    private Location lastLocation;
    private boolean waitingForLocation;

    private LocationManager locationManager;
    private Context context;

    private float lastLocationAccuracy;
    private int ignoredUpdates;

    /**
     * Creates a locationService that starts listening immediately, permissions must be requested before calling.
     *
     * @param context The context that this location service lives in.
     */
    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        waitingForLocation = false;
        lastLocation = null;
        lastLocationAccuracy = Float.MAX_VALUE;
        ignoredUpdates = 0;

        rebuild();
    }

    /**
     * Solicit an update from this location service (calls getCoordinates)
     */
    public void updateLocation() {
        if (lastLocation == null)
            waitingForLocation = true;
        else
            getCoordinates(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    /**
     * Returns true if gps is turned on
     *
     * @return The status of the gps
     */
    public boolean isGPSOn() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * This locationservice starts listening again
     */
    public void rebuild() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, this, null);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }

    /**
     * This location service stops listening
     */
    public void dispose() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    /**
     * Overridable, is sent lat/longitude of the user
     *
     * @param lat   The lattitude of the user
     * @param longi The longittude of the user
     */
    public abstract void getCoordinates(double lat, double longi);

    @Override
    public void onLocationChanged(Location location) {
        if (waitingForLocation) {
            getCoordinates(location.getLatitude(), location.getLongitude());
            lastLocation = location;
            waitingForLocation = false;
        }

        if (location.getAccuracy() < lastLocationAccuracy || ignoredUpdates > MAX_IGNORES) {
            ignoredUpdates = 0;
            lastLocationAccuracy = location.getAccuracy();
            lastLocation = location;
        } else {
            ignoredUpdates++;
        }
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
