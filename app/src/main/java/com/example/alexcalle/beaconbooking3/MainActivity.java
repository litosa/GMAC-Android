package com.example.alexcalle.beaconbooking3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Delegation;
import com.auth0.android.result.UserProfile;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;
//
//import com.auth0.logindemo.R;


public class MainActivity extends AppCompatActivity {

    private Auth0 mAuth0;
    private UserProfile mUserProfile;
    AuthenticationAPIClient aClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        mAuth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));

        aClient = new AuthenticationAPIClient(mAuth0);
        aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mUserProfile = payload;
                            }
                        });
                    }


                    @Override
                    public void onFailure(AuthenticationException error) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Profile Request Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



        Button refreshTokenButton = (Button) findViewById(R.id.refreshTokenButton);
        Button idTokenButton = (Button) findViewById(R.id.tokenIDButton);
        Button logoutButton = (Button) findViewById(R.id.logout);


        refreshTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewIDWithRefreshToken();
            }
        });
        idTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewIDWithOldIDToken();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void getNewIDWithOldIDToken() {
        String idToken = CredentialsManager.getCredentials(this).getIdToken();
        aClient.delegationWithIdToken(idToken).start(new BaseCallback<Delegation, AuthenticationException>() {
            @Override
            public void onSuccess(final Delegation payload) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "New idToken: " + payload.getIdToken(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to get the new idToken", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void getNewIDWithRefreshToken() {
        String refreshToken = CredentialsManager.getCredentials(this).getRefreshToken();
        aClient.delegationWithRefreshToken(refreshToken).start(new BaseCallback<Delegation, AuthenticationException>() {
            @Override
            public void onSuccess(final Delegation payload) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "New idToken: " + payload.getIdToken(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to get the new idToken", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void logout() {
        CredentialsManager.deleteCredentials(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
