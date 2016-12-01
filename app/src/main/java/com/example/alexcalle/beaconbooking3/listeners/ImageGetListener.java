package com.example.alexcalle.beaconbooking3.listeners;

import com.example.alexcalle.beaconbooking3.models.ImageCollection;

/**
 * Created by hogst on 2016-11-29.
 */

public interface ImageGetListener {
    void onImageGetSuccess(ImageCollection imageCollection);
    void onImageGetFailure();
}
