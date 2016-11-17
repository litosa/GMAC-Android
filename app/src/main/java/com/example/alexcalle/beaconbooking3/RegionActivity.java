package com.example.alexcalle.beaconbooking3;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.example.alexcalle.beaconbooking3.estimote.NearableID;
import com.example.alexcalle.beaconbooking3.estimote.Product;
import com.example.alexcalle.beaconbooking3.estimote.ShowroomManager;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionActivity extends AppCompatActivity {


    private BeaconManager _beaconManager;
    private LastBeacon _lastBeacon;
    private EmployeeService _employeeService;
    private String _scanId;
    private static final String TAG = "RegionActivity";

    private ShowroomManager _showroomManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.regionactivity);

        ImageView img = (ImageView)findViewById(R.id.imageAnimation);
        img.setBackgroundResource(R.drawable.animation_list);

        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

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

        Map<NearableID, Product> products = new HashMap<>();
        products.put(new NearableID("9b7397a1b739a8f2"), new Product("Utsikten",
                "Stickers Cykel"));
        products.put(new NearableID("5ce0ed58811d25e8"), new Product("Trossen",
                "Stickers Väska"));
        products.put(new NearableID("c7bb7319f45a7c1f"), new Product("Bojen",
                "Stickers Bil"));
        products.put(new NearableID("07dd2ab8d9dddd61"), new Product("Växthuset",
                "Stickers Hund"));
        products.put(new NearableID("07b221ff4d842506"), new Product("Mellanrummet",
                "Stickers Dörr"));
        products.put(new NearableID("4e15e398f3b7f872"), new Product("Mässen",
                "Stickers Säng"));
        products.put(new NearableID("05d32b684a2ba23b"), new Product("Insikten",
                "Stickers Sko"));
        products.put(new NearableID("ecc412a4f6b5e7ee"), new Product("Brandgula rummet",
                "Stickers Kylskåp"));
        products.put(new NearableID("447824748a361bc5"), new Product("Rutiga rummet",
                "Stickers Ingen logga"));
        products.put(new NearableID("3ef047f82ce6f732"), new Product("Digitala rummet",
                "Stickers Stol"));

        _showroomManager = new ShowroomManager(this, products);

//        _showroomManager.setListener(new ShowroomManager.Listener(){
//            @Override
//            public void onProductPickup(Product product) {
//                ((TextView) findViewById(R.id.titleLabel)).setText(product.getName());
//                ((TextView) findViewById(R.id.descriptionLabel)).setText(product.getSummary());
//                findViewById(R.id.descriptionLabel).setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onProductPutdown(Product product) {
//                ((TextView) findViewById(R.id.titleLabel)).setText("Pick up an object to learn more about it");
//                findViewById(R.id.descriptionLabel).setVisibility(View.INVISIBLE);
//            }
//        });


        _lastBeacon = new LastBeacon(new Date());
        _employeeService = new EmployeeService();

        _beaconManager = new BeaconManager(getApplicationContext());

        _beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                _beaconManager.startRanging(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));
            }
        });

        _beaconManager.setForegroundScanPeriod(5000, 15000);

        _beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                if (!beacons.isEmpty()) {

                    _lastBeacon.lastDiscovery = new Date();
                    int minorId = beacons.get(0).getMinor();

                    checkIfLeftTheBuilding();

                    if (_lastBeacon.minorId != minorId) {

                        _lastBeacon.minorId = minorId;

                        int currentZoneId = minorId;
                        String employeeId = CredentialsManager.getUserId(getApplicationContext());

                        CharSequence text = "Välkommen till " + minorId;
                        showText(text);

                        _employeeService.updateUserZone(currentZoneId, employeeId);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ShowroomManager updates");
            _showroomManager.startUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ShowroomManager updates");
        _showroomManager.stopUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _showroomManager.destroy();
    }


    public void checkIfLeftTheBuilding() {

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        CharSequence text2 = "Hej från pollen!";
                        showText(text2);
                        long diffInSeconds = (new Date().getTime() - _lastBeacon.lastDiscovery.getTime()) / 1000;
                        if (diffInSeconds > 30) {
                            CharSequence text = "Hej då!";
                            showText(text);
                        }
                        if (diffInSeconds > 3600) {
                            CharSequence text = "Du har gått hem!";
                            showText(text);
                            _beaconManager.disconnect();
                        }
                    }
                },
                40000);
    }

    public void showText(CharSequence text) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}
