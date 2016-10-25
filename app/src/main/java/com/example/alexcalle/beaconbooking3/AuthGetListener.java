package com.example.alexcalle.beaconbooking3;

public interface AuthGetListener {
    void onAuthGetSuccess(String token, int expiresIn, String userName);

    void onAuthGetFailure();
}