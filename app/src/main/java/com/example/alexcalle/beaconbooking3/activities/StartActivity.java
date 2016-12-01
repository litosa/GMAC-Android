package com.example.alexcalle.beaconbooking3.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.alexcalle.beaconbooking3.R;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

/**
 * Created by Alex on 10/25/2016.
 */

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startactivity);

        if(CredentialsManager.getCredentials(this).getIdToken() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), RegionActivity.class));
        }
    }
}
