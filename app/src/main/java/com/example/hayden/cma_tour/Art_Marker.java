package com.example.hayden.cma_tour;

import com.google.android.gms.maps.model.LatLng;

public class Art_Marker {
    private String title;
    private String artist;
    private LatLng coords;
    private int floor;
    private String fileLocation;

    public Art_Marker(String title, String artist, double lat, double lng, int floor, String fileLocation) {
        this.title = title;
        this.artist = artist;
        this.coords = new LatLng(lat, lng);
        this.floor = floor;
        this.fileLocation = fileLocation;
    }

    /** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getters and Setters~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getTitle() { return this.title; }

    public String getArtist() {
        return this.artist;
    }

    public LatLng getCoords(){
        return this.coords;
    }

    public int getFloor(){
        return this.floor;
    }

    public String getFileLocation() { return this.fileLocation; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setCoords(double lat, double lng) {
        this.coords = new LatLng(lat, lng);
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
