package com.example.alexcalle.beaconbooking3.utils;

import android.content.Context;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.example.alexcalle.beaconbooking3.Employee;
import com.example.alexcalle.beaconbooking3.EmployeeService;
import com.example.alexcalle.beaconbooking3.RoomGetListener;
import com.example.alexcalle.beaconbooking3.RoomResult;
import com.example.alexcalle.beaconbooking3.RoomService;
import com.example.alexcalle.beaconbooking3.estimote.ShowroomManager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

/**
 * Created by hogst on 2016-11-23.
 */

public class ListenerManager implements RoomGetListener {
    private Helpers _helpers;
    private BeaconManager _beaconManager;
    private String _scanId;
    private int lastNearableRssi = -100;
    private int newNearableRssi;
    private EmployeeService _employeeService;
    private RoomService _roomService;
    private List<RoomResult> _roomResultList;
    private LastHit _lastHit;
    private String _userId;

    public ListenerManager (Context context) {
        _roomService = new RoomService();
        _employeeService = new EmployeeService();
        _helpers = new Helpers();
        _beaconManager = new BeaconManager(context);
        _beaconManager.setForegroundScanPeriod(5000, 15000);
        _roomService.getRoomsInfo(this);
        _userId = CredentialsManager.getUserId(getApplicationContext());
        _lastHit = new LastHit(new Date());

        initalizeListeners();
    }

    public void initalizeListeners() {

        _beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {

                    
                    _lastHit.lastDiscovery = new Date();
                    checkIfLeftTheBuilding();

                    String minorId = String.valueOf(beacons.get(0).getMinor());

                    if (_lastHit.id != minorId) {

                        _lastHit.id = minorId;

                        CharSequence text = "Välkommen till " + minorId;
                        _helpers.showText(text);

                        _employeeService.updateUser(new Employee(_userId, minorId, null));
                    }
                }
            }
        });
        
        _beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {

                if (!nearables.isEmpty()) {
                    Nearable nearable = nearables.get(0);

                    for (Nearable n : nearables) {
                        CharSequence text = "0:an: " + nearable.identifier + " || Aktuell i loop: " + n.identifier + " (rssi: " + n.rssi + ")";
                        _helpers.showText(text);
                    }

                    if (nearable != null) {

                        _lastHit.lastDiscovery = new Date();
                        checkIfLeftTheBuilding();

                        if (nearable.identifier != _lastHit.id) {
                            _lastHit.id = nearable.identifier;

                            _employeeService.updateUser(new Employee(_userId, null, nearable.identifier));

                            CharSequence text = "Välkommen till " + nearable.identifier;
                            _helpers.showText(text);
                        }
                    }
                }
            }
        });
    }

    public void startUpdates() {
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
//        _beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                _scanId = _beaconManager.startNearableDiscovery();
//            }
//        });
    }

    public void destroy() {
        _beaconManager.disconnect();
    }

    @Override
    public void onRoomGetSuccess(RoomResult[] roomResults) {
        _roomResultList = Arrays.asList(roomResults);

        startUpdates();
    }
    
    @Override
    public void onRoomGetFailure() {
        _helpers.showText("Det går inte hämta rooms");
    }

    public void checkIfLeftTheBuilding() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        CharSequence text2 = "Hej från pollen!";
                        _helpers.showText(text2);
                        long diffInSeconds = (new Date().getTime() - _lastHit.lastDiscovery.getTime()) / 1000;
                        if (diffInSeconds > 30) {
                            CharSequence text = "Hej då!";
                            _helpers.showText(text);
                        }
                        if (diffInSeconds > 3600) {
                            CharSequence text = "Du har gått hem!";
                            _helpers.showText(text);
                            _beaconManager.disconnect();
                        }
                    }
                },
                40000);
    }
}
