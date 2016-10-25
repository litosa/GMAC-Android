package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 10/18/2016.
 */

public class BeaconResult {

    public String zone;
    public String room;
    public BeaconType beaconType;
    public boolean hasActiveBooking;

}

enum BeaconType{
    room,
    zone,
    entrance,
    exit
}
