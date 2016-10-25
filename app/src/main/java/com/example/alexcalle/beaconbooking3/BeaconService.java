package com.example.alexcalle.beaconbooking3;

import android.util.Base64;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Alex on 10/18/2016.
 */

public class BeaconService implements RestClientListener {

    private BeaconGetListener _getListener;


    public void getBeaconInfo(int minorId, int majorId, BeaconGetListener getListener) {
        this._getListener = getListener;

        BeaconViewModel bvm = new BeaconViewModel(minorId, majorId);
        Gson gson = new Gson();
        String bvmJson = gson.toJson(bvm);

        String url = "http://beaconbookingapi20161017013752.azurewebsites.net/api/beacons";
        String method = "POST";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");
//        headers.put("Authentication", "bearer " + token);
        headers.put("Authorization", "bearer ");

        RestClient restClient = new RestClient(url, method, headers, bvmJson, this);
        restClient.execute();
    }


    @Override
    public void onResult(RestClientResult result) {
        if(result.responseCode == 200) {
            try {
                Gson gson = new Gson();
                BeaconResult beaconResult = gson.fromJson(result.json, BeaconResult.class);

                this._getListener.onBeaconGetSuccess(beaconResult.room, beaconResult.zone, beaconResult.beaconType, beaconResult.hasActiveBooking);
            } catch (Exception ex) {
                this._getListener.onBeaconGetFailure();
            }
        } else {
            this._getListener.onBeaconGetFailure();
        }
    }

}
