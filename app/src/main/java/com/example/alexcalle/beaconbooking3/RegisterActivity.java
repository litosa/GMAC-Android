package com.example.alexcalle.beaconbooking3;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.cloud.internal.User;

public class RegisterActivity extends Activity implements RegisterGetListener, AdapterView.OnItemSelectedListener {

    private int DepartmentId;
    private Spinner dropdown;
    String[] items = new String[]{"Utvecklare", "Designer"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.registeractivity);

        final EditText etUsername = (EditText) findViewById(R.id.etUsernameRegister);
        etUsername.setTextColor(Color.rgb(255, 255, 255));

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        etEmail.setTextColor(Color.rgb(255, 255, 255));

        final EditText etPasswordRegister = (EditText) findViewById(R.id.etPasswordRegister);
        etPasswordRegister.setTextColor(Color.rgb(255, 255, 255));

        final EditText etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etConfirmPassword.setTextColor(Color.rgb(255, 255, 255));

        dropdown = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        final Button bRegister = (Button) findViewById(R.id.bRegister);
        final TextView loginLink = (TextView) findViewById(R.id.link_to_login);



        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Username = etUsername.getText().toString();
                final String Email = etEmail.getText().toString();
                final String Password = etPasswordRegister.getText().toString();
                final String ConfirmPassword = etConfirmPassword.getText().toString();

                RegisterService service = new RegisterService();

                service.RegisterUser(Username, Email, DepartmentId, Password, ConfirmPassword, RegisterActivity.this);

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    @Override
    public void onRegisterGetSuccess() {

        Context context = getApplicationContext();
        CharSequence text = "Välkommen! Du har Registerat dig!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onRegisterGetFailure() {

        Context context = getApplicationContext();
        CharSequence text = "Användare är upptagen/Felaktiga uppgifter!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent registerIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        position = dropdown.getSelectedItemPosition();
        switch (position) {

            case 0:
                DepartmentId = (int)dropdown.getSelectedItemId()+1;
                break;

            case 1:

                DepartmentId = (int)dropdown.getSelectedItemId()+1;
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing to do here
    }
}