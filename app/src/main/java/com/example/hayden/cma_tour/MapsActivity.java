package com.example.hayden.cma_tour;

import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener,
                    LocationListener, GoogleMap.OnIndoorStateChangeListener,
                    GoogleMap.OnCameraChangeListener {
    private final String TAG = "CMA_MAP";
    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private BufferedWriter mBufferedWriter;
    private File markerFile;
    private Button picButton;
    public static Location mCurrentLocation;

    //for keeping track of what is on the
    public static  ArrayList<Art_Marker> filteredMarkers;
    public static  ArrayList<Art_Marker> allMarkers;

    // Indexes in the CSV
    final static int TITLE = 0;
    final static int ARTIST = 1;
    final static int YEAR = 2;
    final static int GENRE = 3;
    final static int LAT = 4;
    final static int LNG = 5;
    final static int FLOOR = 6;
    final static int FILE_LOCATION = 7;

    //map levels are counted from highest to lowest... level 2 = 0
    final static int LEVEL_2 = 0;
    final static int LEVEL_1 = 1;
    final static int BASEMENT = 2;

    //directories
    public static File IMG_FOLDER;

    private int currentLevel;

    private LatLngBounds mMapBounds;
    private final LatLng NE_BOUND = new LatLng(41.510087, -81.610124);
    private final LatLng SW_BOUND = new LatLng(41.507865, -81.613096);
    private CameraPosition lastKnownCamPosition;
    private final int NEW_FILTER = 100;
    private final int NEW_MARKER = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // set up the button
        picButton = (Button) findViewById(R.id.photobutton);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllMarkers();
            }
        });

        currentLevel = 1;

        //Set up I/O components: Reader, Writer, StringBuilder
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        IMG_FOLDER = new File(dcim, "CMA_IMGS");
        IMG_FOLDER.mkdir();
        markerFile = new File(dcim, "CMUMarkers.csv");
        boolean writeHeadings = !(markerFile.exists());
        try {
            mBufferedWriter = new BufferedWriter(new FileWriter(markerFile, true));

            String headings = "Title,Artist,Year,Style,Latitude,Longitude,Floor,FileLocation\n";
            String entry1   = "Starry Night,Van Gogh,1889,Post-Impressionist,41.508513,-81.611770,1,DCIM/CMA_Photos/1_van\n";
            String entry2   = "Painting 2,Matt Damon,2001,Contemporary,41.508712,-81.611252,2,DCIM/CMA_Photos/2_matt\n";
            String entry3   = "Painting 3,Piet Mondiran,2050,Contemporary,41.509003,-81.611019,2,DCIM/CMA_Photos/2_matt\n";
            String entry4   = "VG2,Van Gogh,1890,Pointalist,41.508720,-81.612125,1,DCIM/CMA_Photos/2_matt\n";

            Log.d(TAG, "Writing headings to CSV");
            //write to CSV and reset SB / flush bufferedwriter
            if (writeHeadings) {
                mBufferedWriter.write(headings);
                mBufferedWriter.write(entry1);
                mBufferedWriter.write(entry2);
                mBufferedWriter.write(entry3);
                mBufferedWriter.write(entry4);
            }

            mBufferedWriter.flush();

        } catch (IOException ex) {
            Log.e(TAG, "Cannot create IO component", ex);
        }

        setUpMapIfNeeded();
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng cmaLoc = new LatLng(41.5089, -81.61169);
        mMapBounds = new LatLngBounds(SW_BOUND, NE_BOUND);
        lastKnownCamPosition = mMap.getCameraPosition();

        mMap.setOnIndoorStateChangeListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cmaLoc, (float) 18.5));

        loadMarkersFromCSV();
    }

    /**
     * Loads the markers from a csv file
     */
    private void loadMarkersFromCSV(){

        String line;
        allMarkers = new ArrayList<Art_Marker>();

        //clear the map markers
        //mMap.clear();

        try {
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(markerFile));
            mBufferedReader.readLine(); //Skip header line
            while ( (line = mBufferedReader.readLine()) != null) {
                String[] info = line.split(",");

                Art_Marker marker =  new Art_Marker(info[TITLE],
                                            info[ARTIST],
                                            Integer.parseInt(info[YEAR]),
                                            info[GENRE],
                                            Double.parseDouble(info[LAT]),
                                            Double.parseDouble(info[LNG]),
                                            Integer.parseInt(info[FLOOR]),
                                            info[FILE_LOCATION],
                                            mMap);

                allMarkers.add(marker);
                addMarkersToMap();
            }


        } catch (IOException ex) {
            Log.e(TAG, "Error reading from file", ex);
        }
        //  create new Art_Marker for it
        //  add it to a Collection of Art_Markers
        //  add the marker to a map
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        Log.i(TAG, "Creating Location Request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Request Location Updates
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onIndoorBuildingFocused() {

    }

    @Override
    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
        currentLevel = indoorBuilding.getActiveLevelIndex();
        Log.d(TAG, "On level: " + indoorBuilding.getActiveLevelIndex());
        if (mCurrentLocation != null) {
            Log.d(TAG, " Lat: " + mCurrentLocation.getLatitude() + "Lng: " + mCurrentLocation.getLongitude());
        }

        addMarkersToMap();
    }


    /**
     * ENSURE THAT CAMERA ALWAYS STAYS WITHIN BOUNDS
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng point = cameraPosition.target;

        if (mMapBounds.contains(point)) {
            lastKnownCamPosition = cameraPosition;
        } else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastKnownCamPosition));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu: this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_opt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.filter_markers: {
                Log.d(TAG, "filter markers selected");
                dispatchNewFilterForm();
                return true;
            }
            case R.id.add_new_marker: {
                Log.d(TAG, "add new markers selected");
                dispatchNewMarkerForm();
                return true;
            }
        }

        return false;
    }

    public void addMarkersToMap(){
        //clear the current map of all markers
        removeAllMarkers();

        if (filteredMarkers != null) {
            Log.d(TAG, "Choosing Filtered Markers");
            for (Art_Marker marker: filteredMarkers){
                if (marker.getFloor() == (2 - currentLevel)){
                    marker.addToMap();
                }
            }
            return;
        }
        //else no filters have been chosen so just add all markers
        for (Art_Marker marker: allMarkers) {
            if (marker.getFloor() == (2 - currentLevel)){
                marker.addToMap();
            }
        }
    }

    public void removeAllMarkers(){
        for (Art_Marker marker: allMarkers) {
            marker.removeFromMap();
        }
    }

    public void dispatchNewFilterForm() {
        Intent formIntent = new Intent(this, FilterActivity.class);
        startActivityForResult(formIntent, NEW_FILTER);
    }

    public void dispatchNewMarkerForm(){
        Intent markerIntent = new Intent(this, NewMarkerActivity.class);
        startActivityForResult(markerIntent, NEW_MARKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_FILTER) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Successfully filtered markers");
                addMarkersToMap();
            }
        }
        else if(requestCode == NEW_MARKER){
            if(resultCode == RESULT_OK){
                addMarkersToMap();
                String entry = data.getStringExtra("CSV_ENTRY");
                Log.d(TAG, "Successfully added marker");
                Log.d(TAG, entry);
                try {
                    mBufferedWriter.write(entry);
                    mBufferedWriter.flush();
                } catch (IOException ex) {
                    Log.e(TAG, "ERROR WRITING NEW ENTRY TO CSV", ex);
                }
            }
        }
    }
}

