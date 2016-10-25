package com.example.alexcalle.beaconbooking3;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Alex on 10/20/2016.
 */

public class RegisterService implements RestClientListener {

    private RegisterGetListener _getListener;

    public void RegisterUser(String userName, String email, int departmentId, String password, String confirmPassword, RegisterGetListener getListener)
    {
        this._getListener = getListener;

        RegisterViewModel rvm = new RegisterViewModel(userName, email, departmentId, password, confirmPassword);
        Gson gson = new Gson();
        String rvmJson = gson.toJson(rvm);

        String url = "http://beaconbookingapi20161017013752.azurewebsites.net/api/users";
        String method = "POST";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");


        RestClient restClient = new RestClient(url, method, headers, rvmJson, this);
        restClient.execute();

    }

    @Override
    public void onResult(RestClientResult result) {
        if(result.responseCode == 200) {
            try {

                this._getListener.onRegisterGetSuccess();

            } catch (Exception ex) {
                this._getListener.onRegisterGetFailure();
            }
        } else {
            this._getListener.onRegisterGetFailure();
        }
    }
}
