package com.example.hayden.cma_tour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Art_Marker {
    private String title;
    private String artist;
    private int year;
    private String genre;
    private LatLng coords;
    private int floor;
    private Uri fileLocation;
    private GoogleMap mMap;
    public boolean active;
    private Marker marker;

    public Art_Marker(String title, String artist, int year, String genre, double lat, double lng,
                      int floor, String fileLocation, GoogleMap mMap) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.genre = genre;
        this.coords = new LatLng(lat, lng);
        this.floor = floor;
        this.fileLocation =  Uri.parse(fileLocation);
        this.mMap = mMap;
        active = false;
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

    public String getFileLocation() { return this.fileLocation.toString(); }

    public int getYear() { return this.year; }

    public String getGenre() { return this.genre; }

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

    /** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End Getters and Setters~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    @Override
    public String toString(){
        return "{(Title: " + getTitle() + "),( Artist: " + getArtist() + "), (Coords: " + getCoords().toString() +
                "), (Year " + getYear() + "), (Genre: " + getGenre() +"), (Floor: " + getFloor() + "), (File Location: " + getFileLocation() +")}";
    }

    /**
     * Add marker to the map only if it isnt already on the map
     */
    public void addToMap () {
        if (!active) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(getCoords())
                    .title(getTitle())
                    .snippet(getArtist()));
            active = true;
        }
    }

    public void addToMapWithImg () {

        Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileLocation.getPath()), 96, 96);

        if (!active) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(getCoords())
                    .title(getTitle())
                    .snippet(getArtist()));
            active = true;
        }
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(imageBitmap));
//        marker.showInfoWindow();
    }


    /**
     * remove from the map if it is on the map
     */
    public void removeFromMap() {
        if (active) {
            marker.remove();
            active = false;
        }
    }

    public void hideMarker() {
        if (marker != null) {
            marker.setVisible(false);
        }
    }

    public void showMarker() {
        if (marker != null) {
            marker.setVisible(true);
        }
    }

    public String getCSVEntry(){
        //String headings = "Title,Artist,Year,Style,Latitude,Longitude,Floor,FileLocation\n";
        return getTitle() + "," +
                getArtist()+ "," +
                getYear() + "," +
                getGenre() + "," +
                getCoords().latitude + ","+
                getCoords().longitude + "," +
                getFloor() + "," +
                getFileLocation() +"\n";
    }
}
