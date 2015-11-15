package com.example.hayden.cma_tour;

import com.google.android.gms.maps.model.LatLng;

public class Art_Marker {
    private String title;
    private String desc;
    private LatLng coords;
    private int floor;

    public Art_Marker(String title, String desc, double lat, double lng, int floor) {
        this.title = title;
        this.desc = desc;
        this.coords = new LatLng(lat, lng);
        this.floor = floor;
    }

    /** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getters and Setters~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public LatLng getCoords(){
        return this.coords;
    }

    public int getFloor(){
        return this.floor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCoords(double lat, double lng) {
        this.coords = new LatLng(lat, lng);
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
