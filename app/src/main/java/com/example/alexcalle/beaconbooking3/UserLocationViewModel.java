package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 10/18/2016.
 */

public class UserLocationViewModel {

    String userName;
    String room;
    String zone;

    public UserLocationViewModel(String room, String zone)
    {
        this.room = room;
        this.zone = zone;
    }
}
