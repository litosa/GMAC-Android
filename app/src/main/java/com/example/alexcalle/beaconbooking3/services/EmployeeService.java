package com.example.alexcalle.beaconbooking3.services;

import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Delegation;
import com.auth0.android.result.UserProfile;
import com.example.alexcalle.beaconbooking3.models.Employee;
import com.example.alexcalle.beaconbooking3.rest.RestClient;
import com.example.alexcalle.beaconbooking3.listeners.RestClientListener;
import com.example.alexcalle.beaconbooking3.rest.RestClientResult;
import com.example.alexcalle.beaconbooking3.listeners.EmployeeGetListener;
import com.example.alexcalle.beaconbooking3.listeners.EmployeePostListener;
import com.example.alexcalle.beaconbooking3.listeners.EmployeePutListener;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

/**
 * Created by Alex on 10/18/2016.
 */

public class EmployeeService implements RestClientListener {

    Auth0 _auth0 = new Auth0("BWJySS4YC58U9l0MnoqWIO2CIictpkG7", "alcagroup.eu.auth0.com");
    public static final String employeeurl = "http://gmac-api.azurewebsites.net/api/employees/";
    private EmployeeGetListener _getListener;
    private EmployeePostListener _postListener;
    private EmployeePutListener _putListener;


    public void updateUser(Employee employee) {
        updateInit(employee);
    }

    public void updateUser(Employee employee, EmployeePutListener putListener) {
        this._putListener = putListener;

        updateInit(employee);
    }

    private void updateInit(final Employee employee) {
        Gson gson = new Gson();
        final String jsonEmployee = gson.toJson(employee);

        final AuthenticationAPIClient aClient = new AuthenticationAPIClient(_auth0);
        aClient.tokenInfo(CredentialsManager.getCredentials(getApplicationContext()).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(UserProfile payload) {
                        Execute(employee._id, jsonEmployee, CredentialsManager.getCredentials(getApplicationContext()).getIdToken());
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        aClient.delegationWithRefreshToken(CredentialsManager.getCredentials(getApplicationContext()).getRefreshToken())
                                .start(new BaseCallback<Delegation, AuthenticationException>() {

                                    @Override
                                    public void onSuccess(Delegation payload) {
                                        Execute(employee._id, jsonEmployee, payload.getIdToken());
                                    }

                                    @Override
                                    public void onFailure(AuthenticationException error) {
                                        Log.d("error", "error: " + error);
                                    }
                                });
                    }
                });
    }


    public void getEmployee(String userId, EmployeeGetListener getListener) {
        this._getListener = getListener;

        String url = employeeurl + userId;
        String method = "GET";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + CredentialsManager.getCredentials(getApplicationContext()).getIdToken());

        RestClient restClient = new RestClient(url, method, headers, this);
        restClient.execute();
    }

    @Override
    public void onResult(RestClientResult result) {
        if(result.responseCode == 200) {
            try {
                Gson gson = new Gson();
                Employee employee = gson.fromJson(result.json, Employee.class);
                this._getListener.onEmployeeGetSuccess(employee);
            } catch (Exception ex) {
                this._getListener.onEmployeeGetFailure();
            }
        }
        else if(result.responseCode == 201) {
            try {
                this._postListener.onEmployeePostSuccess();
            }catch (Exception ex){
                this._postListener.onEmployeePostFailure();
            }
        }
        else if(result.responseCode == 202) {
            if (this._putListener != null)
            {
                try {
                    this._putListener.onEmployeePutSuccess();
                }catch (Exception ex){
                    this._putListener.onEmployeePutFailure();
                }
            }
        }
        else {
            this._getListener.onEmployeeGetFailure();
        }
    }


    public void postEmployee(String userId, String userEmail, String imageUrl, EmployeePostListener postListener) {

        this._postListener = postListener;

        Employee employee = new Employee();
        employee._id = userId;
        employee.email = userEmail;
        employee.imageUrl = imageUrl;

        Gson gson = new Gson();
        String employeeJson = gson.toJson(employee);

        String url = employeeurl;
        String method = "POST";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("Authorization", "bearer " + CredentialsManager.getCredentials(getApplicationContext()).getIdToken());

        RestClient restClient = new RestClient(url, method, headers, employeeJson, this);
        restClient.execute();
    }


    private void Execute(String empId, String jsonEmployee, String idToken){
        String url = employeeurl+empId;
        String method = "PUT";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + idToken);

        RestClient restClient = new RestClient(url, method, headers, jsonEmployee, this);
        restClient.execute();
    }
}
