package com.eli.orange.fragments.HomeFragment;

public class InfoWindowData {
    private String image;

    public InfoWindowData(final String image, final String hotel, final String food, final String transport) {
        this.image = image;
        this.hotel = hotel;
        this.food = food;
        this.transport = transport;
    }

    private String hotel;
    private String food;
    private String transport;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}