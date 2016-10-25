package com.example.alexcalle.beaconbooking3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

public class RegionActivity extends Activity implements BeaconGetListener {


    private BeaconManager beaconManager;
    private static final String TAG = "RegionActivity";
    private String _userName;
    private String _token;
    private int _expiresIn;

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
                Intent intentLogout = new Intent(RegionActivity.this, LoginActivity.class);
                startActivity(intentLogout);

                Context context = getApplicationContext();
                CharSequence text = "Du har loggat ut, Ha en fortsatt trevlig dag!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {

                Beacon beacon = list.get(0);
                int minorId = beacon.getMinor();
                int majorId = beacon.getMajor();

                BeaconService service = new BeaconService();

                service.getBeaconInfo(minorId, majorId, RegionActivity.this);

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


        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "startBeaconMonitoring called");
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));

//                beaconManager.startMonitoring(new Region(
//                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
//                        37493, 63995));


                //beaconManager.startMonitoring(new Region(
                        //"monitored region",
                        //UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        //51863, 32449));
            }
        });
    }


    @Override
    public void onBeaconGetSuccess(String room, String zone, BeaconType beaconType, boolean hasActiveBooking) {
        UserLocationService service = new UserLocationService();

        service.updateUserLocation(room, zone);

        Context context = getApplicationContext();
        CharSequence text = "Du har kommit till " + room + ", zon " + zone;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    @Override
    public void onBeaconGetFailure() {

        Context context = getApplicationContext();
        CharSequence text = "Det går inte att lokalisera dig!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }
}
