package com.home.geofencebackground;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on 12/02/2017.
 */

public class LocationClientService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationClientService.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private List<Geofence> mGeofenceLists = new ArrayList<Geofence>();
    private PendingIntent geofencePendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        createGoogleApi();

        Geofence geofence1 = new Geofence.Builder()
                .setRequestId("your target place")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(-12.046374, -77.042793, 2000.0f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        mGeofenceLists.add(geofence1);

        Log.i(TAG, "geofence added");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if (!googleApiClient.isConnected())
            googleApiClient.connect();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG, "onConnected " + bundle);
        Location l = null;
        try {
            l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (l != null) {
            Log.i(TAG, "lat " + l.getLatitude());
            Log.i(TAG, "lng " + l.getLongitude());
        }

        startLocationUpdate();


        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "lat changed " + location.getLatitude());
        Log.i(TAG, "lng changed " + location.getLongitude());
        LatLng mLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdate() {
        initLocationRequest();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, TransitionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceLists);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, TransitionIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
}
