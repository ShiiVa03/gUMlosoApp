package com.example.gumloso;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {
    private final Integer id;
    private String name;
    private String contact;
    private String address;
    @SerializedName("food_type")
    private String type;
    private int capacity;
    private Float rating;
    private GeoLocation location;
    private byte[] image;
    @SerializedName("timetable")
    private List<DailySchedule> schedule;
    private Boolean favorite;

    public Restaurant(Integer id, String name, String contact, String address, String type, int capacity,
                      Float rating, GeoLocation location, byte[] image, List<DailySchedule> schedule, Boolean favorite) {
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

    public Integer getId() {
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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

    public Boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public static class DailySchedule implements Serializable {
        @SerializedName("weekday")
        int day;
        @SerializedName("opening_time")
        int timeOpen;
        @SerializedName("closing_time")
        int timeClose;

        public DailySchedule(int day, int timeOpen, int timeClose) {
            this.day = day;
            this.timeOpen = timeOpen;
            this.timeClose = timeClose;
        }
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", capacity=" + capacity +
                ", rating=" + rating +
                ", location=" + location +
                ", image=" + "imagem" +
                ", schedule=" + schedule +
                ", favorite=" + favorite +
                '}';
    }
}
