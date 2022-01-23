package com.example.gumloso;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Booking implements Serializable {
    final LocalDateTime date;
    final int numberOfPeople;
    final Restaurant restaurant;
    final String owner;

    public Booking(LocalDateTime date, int numberOfPeople, Restaurant restaurant, String owner) {
        this.date = date;
        this.numberOfPeople = numberOfPeople;
        this.restaurant = restaurant;
        this.owner = owner;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getOwner() {
        return owner;
    }
}
