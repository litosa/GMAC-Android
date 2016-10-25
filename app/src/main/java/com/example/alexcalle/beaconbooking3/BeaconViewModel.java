package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 10/18/2016.
 */

public class BeaconViewModel
{
    String userName;
    int minorId;
    int majorId;

    public BeaconViewModel(int minorId, int majorId)
    {
        this.minorId = minorId;
        this.majorId = majorId;
    }
}
