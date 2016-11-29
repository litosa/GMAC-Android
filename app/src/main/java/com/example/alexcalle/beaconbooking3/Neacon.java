package com.example.alexcalle.beaconbooking3;

/**
 * Created by hogst on 2016-11-29.
 */

public class Neacon
{
    public String id;
    public int rssi;
    public EstimoteType estimoteType;

    public Neacon(String id, int rssi, EstimoteType estimoteType)
    {
        this.id = id;
        this.rssi = rssi;
        this.estimoteType = estimoteType;
    }

    public Neacon(int rssi)
    {
        this.rssi = rssi;
    }


}




