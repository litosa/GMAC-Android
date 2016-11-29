package com.example.alexcalle.beaconbooking3;

import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

/**
 * Created by hogst on 2016-11-29.
 */

public class ImageService implements RestClientListener {

    private ImageGetListener _getListener;
    public static final String imageurl = "http://gmac-api.azurewebsites.net/api/images/sigma_employees";

    public void getImages(ImageGetListener getListener) {
        this._getListener = getListener;

        String method = "GET";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + CredentialsManager.getCredentials(getApplicationContext()).getIdToken());

        RestClient restClient = new RestClient(imageurl, method, headers, this);
        restClient.execute();
    }

    @Override
    public void onResult(RestClientResult result) {
        if(result.responseCode == 200) {
            try {
                Gson gson = new Gson();
                ImageCollection imageCollection = gson.fromJson(result.json, ImageCollection.class);

                this._getListener.onImageGetSuccess(imageCollection);


            } catch (Exception ex) {
                this._getListener.onImageGetFailure();
            }
        }
        else {
            this._getListener.onImageGetFailure();
        }
    }


}
