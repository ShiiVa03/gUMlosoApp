package com.example.gumloso;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {
    private final int id;
    private String name;
    private String contact;
    private String address;
    private String type;
    private int capacity;
    private float rating;
    private GeoLocation location;
    private byte[] image;
    private List<DailySchedule> schedule;
    private boolean favorite;

    public Restaurant(int id, String name, String contact, String address, String type, int capacity,
                      float rating, GeoLocation location, byte[] image, List<DailySchedule> schedule, boolean favorite) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.type = type;
        this.capacity = capacity;
        this.rating = rating;
        this.location = location;
        this.image = image;
        this.schedule = schedule;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<DailySchedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<DailySchedule> schedule) {
        this.schedule = schedule;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public static class DailySchedule implements Serializable {
        int day;
        int timeOpen;
        int timeClose;

        public DailySchedule(int day, int timeOpen, int timeClose) {
            this.day = day;
            this.timeOpen = timeOpen;
            this.timeClose = timeClose;
        }
    }


}
