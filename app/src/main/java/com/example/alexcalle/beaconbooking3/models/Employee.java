package com.example.alexcalle.beaconbooking3.models;

/**
 * Created by Alex on 10/18/2016.
 */

public class Employee {

    public String _id;
    public String email;
    public String imageUrl;
    public String currentZoneId;
    public String currentRoomId;

    public Employee(String id, String currentZoneId, String currentRoomId)
    {
        this._id = id;
        this.currentZoneId = currentZoneId;
        this.currentRoomId = currentRoomId;
    }

    public Employee(){}
}
