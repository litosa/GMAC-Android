package com.example.alexcalle.beaconbooking3.listeners;

import com.example.alexcalle.beaconbooking3.models.Employee;

/**
 * Created by hogst on 2016-11-28.
 */

public interface EmployeeGetListener {
    void onEmployeeGetSuccess(Employee employee);
    void onEmployeeGetFailure();
}
