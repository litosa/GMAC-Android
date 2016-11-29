package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 10/18/2016.
 */

public class Employee {

    String _id;
    String email;
    String imageUrl;
    String currentZoneId;
    String currentRoomId;

    public Employee(String id, String currentZoneId, String currentRoomId)
    {
        this._id = id;
        this.currentZoneId = currentZoneId;
        this.currentRoomId = currentRoomId;
    }

    public Employee(){}
}
