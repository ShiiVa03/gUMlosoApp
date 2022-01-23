package com.example.gumloso;

import java.io.Serializable;

public class GeoLocation implements Serializable {
    double latitude;
    double longitude;

    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static float getDistance(GeoLocation userLocation, GeoLocation location) {
        return (float) Math.sqrt(Math.pow((double) (userLocation.latitude - location.latitude), 2) +
                Math.pow((double) (userLocation.longitude - location.longitude), 2));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
