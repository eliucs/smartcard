package com.internationalmoneygetters.jeevand.firebasetester;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.util.Date;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements GeoQueryEventListener,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    double lat, longi;
    public TextView mTextView, mTriggerTextView;
    Button mButtonSave, mGenB;
    EditText mLocNameField;
    Firebase mRef;
    GeoFire geoFire;
    GeoQuery geoQuery;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLIS = 10;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLIS / 2;
    protected final static String LOCATION_KEY = "location-key";
    protected Location mCurrentLocation;
    protected static final String TAG = "location-updates-sample";
    protected String mLastUpdateTime;
    PowerManager powerManager;
    WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/geofire");
        this.geoFire = new GeoFire(ref);
        this.geoQuery = this.geoFire.queryAtLocation(new GeoLocation(lat, longi), 1);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeUpPls");
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mTextView = (TextView) findViewById(R.id.textView);
        mTriggerTextView = (TextView) findViewById(R.id.locationTriggerText);
        mButtonSave = (Button) findViewById(R.id.saveButton);
        mGenB = (Button) findViewById(R.id.genb);
        mLocNameField = (EditText) findViewById(R.id.locNameField);
        mRef = new Firebase("https://keyring-b58ee.firebaseio.com/condition");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                mTextView.setText(text);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                geoFire.setLocation(mLocNameField.getText().toString(), new GeoLocation(lat, longi));
            }
        });

        mGenB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });


        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    11);
        }




        geoQuery.setCenter(new GeoLocation(lat, longi));
        geoQuery.setRadius(2.0/100);
        this.geoQuery.addGeoQueryEventListener(this);


        wakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        this.geoQuery.removeAllListeners();
        mGoogleApiClient.disconnect();
        wakeLock.release();
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        mTriggerTextView.setText("You have entered: " + key + " at " + location);
    }

    @Override
    public void onKeyExited(String key) {
        mTriggerTextView.setText("You have left: " + key);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed:");
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLIS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() throws SecurityException {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        lat = mCurrentLocation.getLatitude();
        longi = mCurrentLocation.getLongitude();
        geoQuery.setCenter(new GeoLocation(lat, longi));
        mTextView.setText(geoQuery.getCenter().toString() + " rad: " + geoQuery.getRadius()*1000);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {


            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            updateUI();
        }
    }
}
