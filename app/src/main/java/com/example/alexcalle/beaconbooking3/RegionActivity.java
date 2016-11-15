package com.example.alexcalle.beaconbooking3;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RegionActivity extends AppCompatActivity implements BeaconGetListener {


    private BeaconManager beaconManager;
    private static final String TAG = "RegionActivity";
    private GmacBeacon[] _beacons;
    private GmacBeacon _latestBeacon;
    private Region[] _regions;
    private LastBeacon _lastBeacon;
    private EmployeeService _employeeService;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.regionactivity);

        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView)findViewById(R.id.imageAnimation);
        img.setBackgroundResource(R.drawable.animation_list);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();


        Button bLogOut = (Button) findViewById(R.id.bLogOut);

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CredentialsManager.deleteCredentials(RegionActivity.this);
                CredentialsManager.deleteUserId(RegionActivity.this);

                Intent intentLogout = new Intent(RegionActivity.this, LoginActivity.class);
                startActivity(intentLogout);

                CharSequence text = "Du har loggat ut, Ha en fortsatt trevlig dag!";
                showText(text);
                finish();
            }
        });

        _lastBeacon = new LastBeacon();
        _employeeService = new EmployeeService();

        beaconManager = new BeaconManager(getApplicationContext());

//        BeaconService beaconService = new BeaconService();
//        beaconService.getAllBeacons(RegionActivity.this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));
            }
        });

        beaconManager.setForegroundScanPeriod(5000, 15000);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                if (!beacons.isEmpty()) {

                    _lastBeacon.lastDiscovery = new Date();
                    int minorId = beacons.get(0).getMinor();

                    if (_lastBeacon.minorId != minorId) {

                        _lastBeacon.minorId = minorId;

                        int currentZoneId = minorId;
                        String employeeId = CredentialsManager.getUserId(getApplicationContext());

                        CharSequence text = "Välkommen till " + minorId;
                        showText(text);

                        _employeeService.updateUserLocation(currentZoneId, employeeId);
                    }
                }
            }


//        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
//            @Override
//            public void onEnteredRegion(Region region, List<Beacon> beacons) {
//
//                Log.i(TAG, "onEnteredRegion: " + region.getIdentifier());
////                GmacBeacon beacon = getGmacBeacon(beacons.get(0).getMinor());
//                CharSequence text = "Välkommen till " + region.getIdentifier();
////                _latestBeacon = beacon;
//                showText(text);
//            }
//
//            @Override
//            public void onExitedRegion(Region region) {
//
////                GmacBeacon beacon = getGmacBeacon(region.getMinor());
//                Log.i(TAG, "onEnteredRegion: " + region.getIdentifier());
//
//                CharSequence text = "Du har gått ut från " + region.getIdentifier();
////                CharSequence text = "Du har lämnat något";
//                showText(text);
//            }
        });
    }

    @Override
    public void onBeaconGetSuccess(final GmacBeacon[] beacons) {

//        _beacons = beacons;
//
//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                beaconManager.startRanging(new Region(
//                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
//                        null, null));
//
//            }
//        });

//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                beaconManager.startMonitoring(new Region("A", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, 16901));
//                beaconManager.startMonitoring(new Region("B", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, 11052));
//
//            }
//        });
    }

    @Override
    public void onBeaconGetFailure() {

    }

//    public GmacBeacon getGmacBeacon(int minorId){
//        for(int i = 0; i < _beacons.length; i++){
//            if (_beacons[i]._id == minorId){
//                return _beacons[i];
//            }
//        }
//        return null;
//    }

    public void checkIfLeftTheBuilding() {
        long diffInSeconds = (new Date().getTime() - _lastBeacon.lastDiscovery.getTime()) / 1000;
        if (diffInSeconds > 30) {
            CharSequence text = "Hej då!";
            showText(text);
            //Disconnecta kanske
        }
    }

    public void showText(CharSequence text) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}
