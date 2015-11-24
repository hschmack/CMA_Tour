package com.example.hayden.cma_tour;

import android.location.Location;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private final String TAG = "CMU_MAP";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;
    private File markerFile;
    private StringBuilder mStringBuilder;
    private Button picButton;
    private Location mCurrentLocation;

    private ArrayList<Art_Marker> markers;

    // Indexes in the CSV
    final static int TITLE = 0;
    final static int ARTIST = 1;
    final static int LAT = 2;
    final static int LNG = 3;
    final static int FLOOR = 4;
    final static int FILE_LOCATION = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        buildGoogleApiClient();

        // set up the button
        picButton = (Button) findViewById(R.id.photobutton);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarkersFromCSV();
            }
        });

        //Set up I/O components: Reader, Writer, StringBuilder
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        markerFile = new File(dcim, "CMUMarkers.csv");
        mStringBuilder = new StringBuilder();
        try {
            mBufferedWriter = new BufferedWriter(new FileWriter(markerFile));
            mBufferedReader = new BufferedReader(new FileReader(markerFile));

            String headings = "Title,Artist,Latitude,Longitude,Floor,FileLocation\n";
            String entry1   = "Starry Night,Van Gogh,10,10,1,DCIM/CMA_Photos/1_van\n";
            String entry2   = "Painting 2,Matt Damon,-10,-10,2,DCIM/CMA_Photos/2_matt\n";

            Log.d(TAG, "Writing headings to CSV");
            //write to CSV and reset SB / flush bufferedwriter
            mBufferedWriter.write(headings);
            mBufferedWriter.write(entry1);
            mBufferedWriter.write(entry2);

            mStringBuilder.setLength(0);
            mBufferedWriter.flush();

        } catch (IOException ex) {
            Log.e(TAG, "Cannot create IO component", ex);
        }
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    /**
     * Loads the markers from a csv file
     * each line follows: Name,Description,Lat,Long,Floor
     */
    private void loadMarkersFromCSV(){
        String line;
        markers = new ArrayList<Art_Marker>();

        try {
            mBufferedReader.readLine(); //Skip header line
            while ( (line = mBufferedReader.readLine()) != null) {
                //Line structure is: Title, Artist, Lat, Lng
                String[] info = line.split(",");
                Log.d(TAG, "Title: "+info[TITLE] + ", Artist: "+info[ARTIST] + ", Lat: "+info[LAT]
                        + ", Lng: "+info[LNG] + ", Floor: "+info[FLOOR] + ", File location: "+info[FILE_LOCATION]);

                Art_Marker marker =  new Art_Marker(info[TITLE],
                                            info[ARTIST],
                                            Double.parseDouble(info[LAT]),
                                            Double.parseDouble(info[LNG]),
                                            Integer.parseInt(info[FLOOR]),
                                            info[FILE_LOCATION]);
                markers.add(marker);
                mMap.addMarker(new MarkerOptions().position(marker.getCoords()).title(marker.getTitle()));
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
}
