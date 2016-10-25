package com.example.alexcalle.beaconbooking3;

import android.util.Base64;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

public class AuthService implements RestClientListener {

    private AuthGetListener _getListener;

    public void login(String username, String password, AuthGetListener getListener) {
        this._getListener = getListener;

        LoginViewModel lvm = new LoginViewModel(username, password);
        Gson gson = new Gson();
        String lvmJson = gson.toJson(lvm);

        String url = "http://beaconbookingapi20161017013752.azurewebsites.net/api/jwt";
        String method = "POST";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");

        RestClient restClient = new RestClient(url, method, headers, lvmJson, this);
        restClient.execute();
    }


    @Override
    public void onResult(RestClientResult result) {
        if(result.responseCode == 200) {
            try {

                Gson gson = new Gson();

                AuthResult authResult = gson.fromJson(result.json, AuthResult.class);
                this._getListener.onAuthGetSuccess(authResult.accessToken, authResult.expiresIn, authResult.userName);
            } catch (Exception ex) {
                this._getListener.onAuthGetFailure();
            }
        } else {
            this._getListener.onAuthGetFailure();
        }
    }
}