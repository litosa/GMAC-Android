package com.example.alexcalle.beaconbooking3.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.ParameterBuilder;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.alexcalle.beaconbooking3.models.Employee;
import com.example.alexcalle.beaconbooking3.models.ImageCollection;
import com.example.alexcalle.beaconbooking3.services.ImageService;
import com.example.alexcalle.beaconbooking3.listeners.EmployeeGetListener;
import com.example.alexcalle.beaconbooking3.listeners.EmployeePostListener;
import com.example.alexcalle.beaconbooking3.listeners.ImageGetListener;
import com.example.alexcalle.beaconbooking3.services.EmployeeService;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

import java.util.Map;
import java.util.Objects;


public class LoginActivity extends Activity implements EmployeeGetListener, EmployeePostListener, ImageGetListener {

    private EmployeeService _employeeService;
    private Lock lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _employeeService = new EmployeeService();

        Auth0 auth0 = new Auth0("BWJySS4YC58U9l0MnoqWIO2CIictpkG7", "alcagroup.eu.auth0.com");
        _auth0 = auth0;

        ParameterBuilder builder = ParameterBuilder.newBuilder();
        Map<String, Object> authenticationParameters = builder.setScope("openid offline_access").asDictionary();
        lock = Lock.newBuilder(auth0, callback)
                .withAuthenticationParameters(authenticationParameters)
                .build(this);
        startActivity(lock.newIntent(this));
    }

    Auth0 _auth0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }

    private final LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(final Credentials credentials) {
            // Login Success response
            AuthenticationAPIClient client = new AuthenticationAPIClient(_auth0);
            client.tokenInfo(credentials.getIdToken())
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(final UserProfile payload) {
                            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
                            CredentialsManager.saveUserId(LoginActivity.this, payload.getId());
                            CredentialsManager.saveUserEmail(LoginActivity.this, payload.getEmail());

                            _employeeService.getEmployee(payload.getId(), LoginActivity.this);
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                        }
                    });
            finish();
        }

        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(), "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(getApplicationContext(), "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onEmployeeGetSuccess(Employee employee) {

        if (employee != null){
            startActivity(new Intent(LoginActivity.this, RegionActivity.class));
        }

        else{
            ImageService imageService = new ImageService();
            imageService.getImages(this);
        }
    }

    @Override
    public void onEmployeeGetFailure() {
    }

    @Override
    public void onEmployeePostSuccess() {
        startActivity(new Intent(LoginActivity.this, RegionActivity.class));
    }

    @Override
    public void onEmployeePostFailure() {
    }

    @Override
    public void onImageGetSuccess(ImageCollection imageCollection) {
        String employeeUrl = CredentialsManager.getUserEmail(this).split("@")[0].replace(".", "_");

        boolean exists = imageSourceExists(employeeUrl, imageCollection);
        String imageUrl;
        if (exists)
        {
            imageUrl = "assets/img/employees/" + employeeUrl + ".jpg";
        }
        else {
            imageUrl = "assets/img/employees/default_user.jpg";
        }
        String userId = CredentialsManager.getUserId(getApplicationContext());
        String userEmail = CredentialsManager.getUserEmail(getApplicationContext());

        _employeeService.postEmployee(userId, userEmail, imageUrl, this);
    }

    @Override
    public void onImageGetFailure() {

    }

    private boolean imageSourceExists(String employeeUrl, ImageCollection imageCollection)
    {
        for (int i = 0; i < imageCollection.sources.length; i++)
        {
            if (Objects.equals(imageCollection.sources[i], employeeUrl))
            {
                return true;
            }
        }
        return false;
    }
}