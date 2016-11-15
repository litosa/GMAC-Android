package com.example.alexcalle.beaconbooking3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private Lock lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Auth0 auth0 = new Auth0("xTgBLq0TU9tjnXLA3rWHlrJaCm1OnOxD", "alcagroup.eu.auth0.com");
        _auth0 = auth0;
        lock = Lock.newBuilder(auth0, callback)
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
        public void onAuthentication(Credentials credentials) {
            // Login Success response
            AuthenticationAPIClient client = new AuthenticationAPIClient(_auth0);
            client.tokenInfo(credentials.getIdToken())
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(final UserProfile payload) {
                            CredentialsManager.saveUserId(LoginActivity.this, payload.getId());
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {


                        }
                    });

            Toast.makeText(getApplicationContext(), "Välkommen!", Toast.LENGTH_SHORT).show();
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
            startActivity(new Intent(LoginActivity.this, RegionActivity.class));
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
}