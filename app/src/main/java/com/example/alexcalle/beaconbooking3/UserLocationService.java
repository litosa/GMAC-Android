package com.example.alexcalle.beaconbooking3;

import android.util.Base64;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Alex on 10/18/2016.
 */

public class UserLocationService {
    public void updateUserLocation(String userName, String room, String zone, String token)
    {

        UserLocationViewModel ulvm = new UserLocationViewModel(userName, room, zone);
        Gson gson = new Gson();
        String ulvmJson = gson.toJson(ulvm);

        String url = "http://beaconbookingapi20161017013752.azurewebsites.net/api/locations";
        String method = "PUT";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + token);

        RestClient restClient = new RestClient(url, method, headers, ulvmJson, null);
        restClient.execute();
    }
}
