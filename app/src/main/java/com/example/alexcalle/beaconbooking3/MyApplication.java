package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 11/17/2016.
 */

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: put your App ID and App Token here
        // You can get them by adding your app on https://cloud.estimote.com/#/apps
        EstimoteSDK.initialize(getApplicationContext(), "alexander-litos-gmail-com--cr7", "a55203d1603bc3270eb84d7b6313a6e6");

        // uncomment to enable debug-level logging
        // it's usually only a good idea when troubleshooting issues with the Estimote SDK
        // EstimoteSDK.enableDebugLogging(true);
    }
}
