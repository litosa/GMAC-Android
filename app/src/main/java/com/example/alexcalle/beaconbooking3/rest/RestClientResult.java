package com.example.alexcalle.beaconbooking3.rest;

public class RestClientResult {
    public String method;

    public int responseCode;

    public String json;

    public RestClientResult(String method, int responseCode, String json) {
        this.method = method;
        this.responseCode = responseCode;
        this.json = json;
    }
}
