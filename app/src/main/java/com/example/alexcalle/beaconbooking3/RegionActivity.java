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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RegionActivity extends AppCompatActivity implements BeaconGetListener, EmployeeGetListener, FragmentEmployee.OnFragmentInteractionListener {


    private BeaconManager beaconManager;
    private static final String TAG = "RegionActivity";
    private String _userName;
//    private GmacBeacon[] _beacons;
    List<GmacBeacon> _beacons;
    private String _employeeId;

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
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                finish();
            }
        });

        final EmployeeService employeeService = new EmployeeService();
        employeeService.getEmployees(RegionActivity.this);

        BeaconService beaconService = new BeaconService();
        beaconService.getAllBeacons(RegionActivity.this);


        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                Coords coordinates = new Coords();

                for (int i = 0; i < list.size(); i++) {

                    GmacBeacon beacon = getGmacBeacon(list.get(i).getMinor());

                    if (beacon != null) {
                        Coords coordsBasedOnRssi = getCoordsBasedOnRssi(beacon.coordinates, list.get(i).getRssi());
                        coordinates.latitude += coordsBasedOnRssi.latitude;
                        coordinates.longitude += coordsBasedOnRssi.longitude;
                    }
                    else {
                        Log.d(TAG, "MinorId: " + list.get(i).getMinor());
                    }

//                    Log.d(TAG, "MinorId: " + list.get(i).getMinor() + " || Power: " + list.get(i).getMeasuredPower() + " || Rssi: " + list.get(i).getRssi() + " || Mac: " + list.get(i).getMacAddress());
                    Log.d(TAG, "MinorId: " + list.get(0).getMinor() + " || Power: " + list.get(0).getMeasuredPower() + " || Rssi: " + list.get(0).getRssi() + " || Mac: " + list.get(0).getMacAddress());

//                    int minorId = beacon.getMinor();
                }
                if (list.size() > 0){
                    employeeService.updateUserLocation(coordinates, CredentialsManager.getUserId(RegionActivity.this));
                }
            }
        });
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {


                Log.d(TAG, "Antal Beacons: " + list.size());

                Beacon beacon = list.get(0);
                int minorId = beacon.getMinor();
                int majorId = beacon.getMajor();

//                ZoneService service = new ZoneService();
//                service.getZoneInfo(majorId, RegionActivity.this);

                BeaconService service = new BeaconService();
                service.getAllBeacons(RegionActivity.this);

            }


            @Override
            public void onExitedRegion(Region region) {
                Context context = getApplicationContext();
                CharSequence text = "Hej då!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });


//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
////                Log.d(TAG, "startBeaconMonitoring called");
////                beaconManager.startMonitoring(new Region(
////                        "monitored region",
////                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
////                        null, null));
//                Log.d(TAG, "startRanging called");
//                beaconManager.startRanging(new Region(
//                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
//                        null, null));
//
//            }
//        });
    }

//    @Override
//    public void onZoneGetSuccess(final String zoneName) {
//
//        Auth0 auth0 = new Auth0("xTgBLq0TU9tjnXLA3rWHlrJaCm1OnOxD", "alcagroup.eu.auth0.com");
//        AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
//
//        client.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
//                .start(new BaseCallback<UserProfile, AuthenticationException>() {
//                    @Override
//                    public void onSuccess(final UserProfile payload){
//
//                        RegionActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                EmployeeService service = new EmployeeService();
//
//                                service.updateUserLocation(zoneName, payload.getId());
//
//                                Context context = getApplicationContext();
//                                CharSequence text = "Du har kommit till zon " + zoneName;
//                                int duration = Toast.LENGTH_LONG;
//
//                                Toast toast = Toast.makeText(context, text, duration);
//                                toast.show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(AuthenticationException error){
//
//                    }
//                });
//
//    }

//    @Override
//    public void onZoneGetFailure() {
//
//        Context context = getApplicationContext();
//        CharSequence text = "Det går inte att lokalisera dig!";
//        int duration = Toast.LENGTH_LONG;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//
//    }

    @Override
    public void onFragmentInteraction(int position) {
        Log.d("TAG", "MainActivity Log " + position + " selected");

        startActivity(new Intent(RegionActivity.this, SendEmailActivity.class));

    }

//    @Override
//    public void onBeaconGetSuccess(final Beacon[] beacons) {
//
//        if (beacons != null) {
//            Auth0 auth0 = new Auth0("xTgBLq0TU9tjnXLA3rWHlrJaCm1OnOxD", "alcagroup.eu.auth0.com");
//            AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
//
//            client.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
//                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
//                        @Override
//                        public void onSuccess(final UserProfile payload) {
//
//                            RegionActivity.this.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    EmployeeService service = new EmployeeService();
//
//                                    service.updateUserLocation(beacon.coordinates, payload.getId());
//
//                                    Context context = getApplicationContext();
//                                    CharSequence text = "Du har blivit träffad av " + beacon.name;
//                                    int duration = Toast.LENGTH_LONG;
//
//                                    Toast toast = Toast.makeText(context, text, duration);
//                                    toast.show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onFailure(AuthenticationException error) {
//                            Context context = getApplicationContext();
//                            CharSequence text = "Det går inte att lokalisera dig!";
//                            int duration = Toast.LENGTH_LONG;
//
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
//
//                        }
//                    });
//        }
//    }


    @Override
    public void onBeaconGetSuccess(final GmacBeacon[] beacons) {

//        _beacons = beacons;

        _beacons = Arrays.asList(beacons);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));
            }
        });
    }

    @Override
    public void onBeaconGetFailure() {

    }

    Employee[] _employees;

    Employee[] get() {return _employees; }


    @Override
    public void onEmployeeGetSuccess(Employee[] employees) {
        this._employees = employees;
    }

    @Override
    public void onEmployeeGetFailure() {

    }


    public Coords getCoordsBasedOnRssi(Coords coords, int rssi) {

        Coords coordsBasedOnRssi = new Coords();

        double loss = (double)-rssi / 100;

        double lat = coords.latitude - coords.latitude * loss;
        double lon = coords.longitude - coords.longitude * loss;

        coordsBasedOnRssi.latitude = (int)lat;
        coordsBasedOnRssi.longitude = (int)lon;

        return coordsBasedOnRssi;
    }

    public GmacBeacon getGmacBeacon(int minorId){
        for(int i = 0; i < _beacons.size(); i++){
            if (_beacons.get(i)._id == minorId){
                return _beacons.get(i);
            }
        }
        return null;
    }
}
