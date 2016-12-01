package com.example.alexcalle.beaconbooking3.estimotes;

import android.content.Context;
import android.widget.Toast;
import android.os.Handler;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.example.alexcalle.beaconbooking3.models.Employee;
import com.example.alexcalle.beaconbooking3.models.Neacon;
import com.example.alexcalle.beaconbooking3.services.EmployeeService;
import com.example.alexcalle.beaconbooking3.utils.CredentialsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

/**
 * Created by hogst on 2016-11-23.
 */

public class ListenerManager {
    private BeaconManager _beaconManager;
    private EmployeeService _employeeService;
    private String _userId;
    private boolean _isRunning;
    private String _lastId;
    private Runnable _updateUserRunner;
    private Runnable _updateUserStateRunner;
    private Handler _handler;
    private List<Neacon> _neacons;

    public ListenerManager (Context context) {

        _neacons = new ArrayList<Neacon>();
        _employeeService = new EmployeeService();
        _beaconManager = new BeaconManager(context);
        _beaconManager.setForegroundScanPeriod(5000, 15000);
        _userId = CredentialsManager.getUserId(getApplicationContext());
        _handler = new Handler();

        initalizeListeners();
        initalizeRunners();
    }

    public void initalizeListeners() {

        _beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {

                    Beacon beacon = beacons.get(0);
                    _neacons.add(new Neacon(String.valueOf(beacon.getMinor()), beacon.getRssi(), EstimoteType.Beacon));

                    if (!_isRunning)
                    {
                        scheduleUserUpdate();
                    }
                }
            }
        });

        _beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {

                if (!nearables.isEmpty()) {

                    Nearable nearable = nearables.get(0);
                    _neacons.add(new Neacon(nearable.identifier, nearable.rssi, EstimoteType.Nearable));

                    if (!_isRunning)
                    {
                        scheduleUserUpdate();
                    }
                }
            }
        });

        _beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                _beaconManager.startRanging(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));

                _beaconManager.startNearableDiscovery();
            }
        });
    }

    public void destroy() {
        _handler.removeCallbacks(_updateUserRunner);
        _handler.removeCallbacks(_updateUserStateRunner);
        _beaconManager.disconnect();
    }

    public void scheduleUserUpdate()
    {
        _isRunning = true;

        _handler.postDelayed(_updateUserRunner, 15000);
    }

    private void initalizeRunners()
    {
        _updateUserRunner = new Runnable() {
            public void run() {
                Neacon neacon = getClosestNeacon();

                if (neacon != null && _lastId != neacon.id)
                {
                    _lastId = neacon.id;

                    if (neacon.estimoteType == EstimoteType.Beacon)
                    {
                        _employeeService.updateUser(new Employee(_userId, neacon.id, null));
                    }

                    else
                    {
                        _employeeService.updateUser(new Employee(_userId, null, neacon.id));
                    }
                }

                _neacons = new ArrayList<Neacon>();
                _isRunning = false;

                scheduleUserState();
            }
        };

        _updateUserStateRunner = new Runnable() {
            public void run() {
                if (_neacons.isEmpty())
                {
                    _employeeService.updateUser(new Employee(_userId, "0", "0"));

                    CharSequence text = "Gick inte lokalisera dig";
                    showText(text);
                }
            }
        };
    }

    public void scheduleUserState() {
        _handler.postDelayed(
                _updateUserStateRunner,
                30000);
    }

    public void showText(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    private Neacon getClosestNeacon()
    {
        List<Neacon> mergedNeacons = new ArrayList<Neacon>();

        for (int i = 0; i < _neacons.size(); i++)
        {
            if (mergedNeaconsContainsId(mergedNeacons, _neacons.get(i).id))
            {
                Neacon neacon = getMergedNeacon(mergedNeacons, _neacons.get(i).id);
                neacon.rssi = (neacon.rssi + _neacons.get(i).rssi) / 2;
            }
            else
            {
                mergedNeacons.add(_neacons.get(i));
            }
        }

        return getClosestMergedNeacon(mergedNeacons);
    }

    private boolean mergedNeaconsContainsId(List<Neacon> mergedNeacons, String id)
    {
        for (int i = 0; i < mergedNeacons.size(); i++)
        {
            if (mergedNeacons.get(i).id == id)
            {
                return true;
            }
        }
        return false;
    }

    private Neacon getMergedNeacon(List<Neacon> mergedNeacons, String id)
    {
        for (int i = 0; i < mergedNeacons.size(); i++)
        {
            if (mergedNeacons.get(i).id == id)
            {
                return mergedNeacons.get(i);
            }
        }
        return null;
    }

    private Neacon getClosestMergedNeacon(List<Neacon> mergedNeacons)
    {
        Neacon closestMergedNeacon = new Neacon(-100);

        for (int i = 0; i < mergedNeacons.size(); i++)
        {
            Neacon mergedNeacon = mergedNeacons.get(i);

            if (mergedNeacon.rssi > closestMergedNeacon.rssi)
            {
                closestMergedNeacon.id = mergedNeacon.id;
                closestMergedNeacon.rssi = mergedNeacon.rssi;
            }
        }

        return getMergedNeacon(mergedNeacons, closestMergedNeacon.id);
    }
}
