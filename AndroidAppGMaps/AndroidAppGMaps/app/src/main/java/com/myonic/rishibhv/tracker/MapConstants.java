package com.myonic.rishibhv.tracker;

public interface MapConstants {
    public static final String ROUTE_LEGS = "legs";
    public static final String ROUTE_STEPS = "steps";
    public static final String POLYLINE = "polyline";
    public static final String POINTS = "points";
    public static final String ROUTES = "routes";
    public static final String REST_API = "https://maps.googleapis.com/maps/api/directions/";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 10000;
}
