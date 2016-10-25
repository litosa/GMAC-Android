package com.example.alexcalle.beaconbooking3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements AuthGetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.loginactivity);


        final EditText etUsername = (EditText) findViewById(R.id.etUsernameLogin);
        etUsername.setTextColor(Color.rgb(255, 255, 255));

        final EditText etPassword = (EditText) findViewById(R.id.etPasswordLogin);
        etPassword.setTextColor(Color.rgb(255, 255, 255));

        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.link_to_register);


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                AuthService service = new AuthService();

                service.login(username, password, LoginActivity.this);

            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    @Override
    public void onAuthGetSuccess(String token, int expiresIn, String userName) {

        Context context = getApplicationContext();
        CharSequence text = "Välkommen " + userName + " Du har loggat in!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        String _token = token;
        int _expiresIn = expiresIn;
        String _userName = userName;

        Intent estimoteIntent = new Intent(LoginActivity.this, EstimoteActivity.class);
        Bundle extras = new Bundle();
        extras.putString("userName", _userName);
        extras.putString("token", _token);
        extras.putInt("expiresIn", _expiresIn);

        estimoteIntent.putExtras(extras);

        startActivity(estimoteIntent);
    }

    @Override
    public void onAuthGetFailure() {

        Context context = getApplicationContext();
        CharSequence text = "Felaktigt användarnamn eller lösenord!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
