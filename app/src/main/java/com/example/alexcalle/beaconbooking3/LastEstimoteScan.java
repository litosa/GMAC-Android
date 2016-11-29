package com.example.alexcalle.beaconbooking3;

import java.util.Date;

/**
 * Created by hogst on 2016-11-23.
 */

public class LastEstimoteScan {

    public String id;
    public Date lastDiscovery;
    public int beaconRssi;
    public int nearableRssi;

    public LastEstimoteScan(Date lastDiscovery, int beaconRssi, int nearableRssi){
        this.lastDiscovery = lastDiscovery;
        this.beaconRssi = beaconRssi;
        this.nearableRssi = nearableRssi;
    }
}
