package com.example.alexcalle.beaconbooking3;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.example.alexcalle.beaconbooking3.estimote.ShowroomManager;
import com.example.alexcalle.beaconbooking3.estimote.ShowzoneManager;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;
import com.example.alexcalle.beaconbooking3.utils.Helpers;
import com.example.alexcalle.beaconbooking3.utils.ListenerManager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

public class RegionActivity extends AppCompatActivity {

    private Helpers _helpers;
    private BeaconManager _beaconManager;
    private LastBeacon _lastBeacon;
    private EmployeeService _employeeService;
    private String _scanId;
    private static final String TAG = "RegionActivity";

    private ShowroomManager _showroomManager;
    private ShowzoneManager _showzoneManager;

    private ListenerManager _listenerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInit();


        _listenerManager = new ListenerManager(this);
//        _showroomManager = new ShowroomManager(this);
//        _showzoneManager = new ShowzoneManager(this);

//        _showzoneManager.startUpdates();

        //AS VIKTIGT Sätter permission! Frågar om tillåtelse att köra android.permission.ACCESS_COARSE_LOCATION
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _listenerManager.destroy();
//        _beaconManager.disconnect();
//        _showroomManager.destroy();
//        _showzoneManager.destroy();
    }

    public void appInit(){
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
                _helpers.showText(text);
                finish();
            }
        });
    }


}
