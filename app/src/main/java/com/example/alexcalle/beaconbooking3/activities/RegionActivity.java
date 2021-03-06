package com.example.alexcalle.beaconbooking3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.example.alexcalle.beaconbooking3.models.Employee;
import com.example.alexcalle.beaconbooking3.R;
import com.example.alexcalle.beaconbooking3.listeners.EmployeePutListener;
import com.example.alexcalle.beaconbooking3.estimotes.ListenerManager;
import com.example.alexcalle.beaconbooking3.services.EmployeeService;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

public class RegionActivity extends AppCompatActivity implements EmployeePutListener {

    private BeaconManager _beaconManager;
    private EmployeeService _employeeService;
    private String _scanId;
    private static final String TAG = "RegionActivity";

    private ListenerManager _listenerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInit();

        _listenerManager = new ListenerManager(this);
        _employeeService = new EmployeeService();

        //Frågar om tillåtelse att köra android.permission.ACCESS_COARSE_LOCATION
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void appInit(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.regionactivity);

        Button bLogOut = (Button) findViewById(R.id.bLogOut);

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listenerManager.destroy();
                logout();
            }
        });
    }

    private void logout()
    {
        _employeeService.updateUser(new Employee(CredentialsManager.getUserId(this), "0", "0"), this);
    }

    @Override
    public void onEmployeePutSuccess() {
        CredentialsManager.deleteCredentials(RegionActivity.this);
        CredentialsManager.deleteUserId(RegionActivity.this);
        CredentialsManager.deleteUserEmail(RegionActivity.this);

        Intent intentLogout = new Intent(RegionActivity.this, LoginActivity.class);
        startActivity(intentLogout);

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), "Du har loggat ut, Ha en fortsatt trevlig dag!", duration);
        toast.show();

        finish();
    }

    @Override
    public void onEmployeePutFailure() {
    }
}
