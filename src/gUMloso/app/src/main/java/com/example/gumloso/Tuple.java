package com.example.gumloso;

import java.io.Serializable;

public class Tuple implements Serializable {
    final String name;
    final double lat;
    final double lon;


    public Tuple(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
