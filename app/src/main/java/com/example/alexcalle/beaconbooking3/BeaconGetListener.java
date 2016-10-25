package com.example.alexcalle.beaconbooking3;

public interface BeaconGetListener {
    void onBeaconGetSuccess(String room, String zone, BeaconType beaconType, boolean hasActiveBooking);

    void onBeaconGetFailure();
}
