package com.example.hayden.cma_tour;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class FilterActivity extends Activity {
    private Button mSubmitButton;

    private final String TAG = "CMA_MAP";
    private Spinner mArtistSpinner;
    private Spinner mStyleSpinner;
    private EditText lowerBound;
    private EditText upperBound;

    private ArrayList<String> artists;
    private ArrayList<String> styles;

    private String artistFilter;
    private String styleFilter;

    final static int UPPER_ONLY = 1;
    final static int LOWER_ONLY = 2;
    final static int BOTH_BOUNDS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        MapsActivity.filteredMarkers = new ArrayList<Art_Marker>();

        // set up the UI stuff
        mArtistSpinner = (Spinner) findViewById(R.id.artist_spinner);
        mStyleSpinner  = (Spinner) findViewById(R.id.style_spinner);
        lowerBound     = (EditText) findViewById(R.id.year_lower_bound);
        upperBound     = (EditText) findViewById(R.id.year_upper_bound);

        mArtistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artistFilter = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });

        mStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                styleFilter = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });

        mSubmitButton = (Button) findViewById(R.id.Submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAllMarkers();
                Log.d(TAG, "FINISHING FORMACTIVITY");
                setResult(RESULT_OK);
                finish();
            }
        });
        artists = new ArrayList<String>();
        artists.add("No Selection");
        styles = new ArrayList<String>();
        styles.add("No Selection");
        setUpSpinners();
    }

    /**
     * iterate through all markers, adding their String categories to corresponding
     * arraylists: artists and styles
     */
    private void setUpSpinners() {
        for(Art_Marker art: MapsActivity.allMarkers) {
            if(!artists.contains(art.getArtist())) {
                artists.add(art.getArtist());
            }

            if(!styles.contains(art.getGenre())){
                styles.add(art.getGenre());
            }
        }
        ArrayAdapter<String> artistSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, artists);
        ArrayAdapter<String> styleSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, styles);

        mArtistSpinner.setAdapter(artistSpinnerAdapter);
        mStyleSpinner.setAdapter((styleSpinnerAdapter));
    }

    private void filterAllMarkers() {
        int yearBounds = -1;
        //determine if the year selectors are used
        if (lowerBound.getText().toString().length()!= 0 && upperBound.getText().toString().length() != 0){
            yearBounds = BOTH_BOUNDS;
        }
        else if (lowerBound.getText().toString().length()== 0 && upperBound.getText().toString().length() != 0){
            yearBounds = UPPER_ONLY;
        }
        else if (lowerBound.getText().toString().length()!= 0 && upperBound.getText().toString().length() == 0) {
            yearBounds = LOWER_ONLY;
        }

        for (Art_Marker art: MapsActivity.allMarkers) {
            if(markerPassesFilter(art, yearBounds)) {
                MapsActivity.filteredMarkers.add(art);
            }
        }
        Log.d(TAG, "ARTIST SELECTED: " + artistFilter);
        Log.d(TAG, "STYLE SELECTED: " + styleFilter);
        Log.d(TAG, "YEAR BOUNDS: " + lowerBound.getText().toString() + "<->" + upperBound.getText().toString());
    }

    private boolean markerPassesFilter(Art_Marker marker, int bounds) {
        //see if marker passes artist filter
        if (!artistFilter.equals("No Selection") && !marker.getArtist().equals(artistFilter)) {
            return false;
        }

        //see if marker passes style filter
        if (!styleFilter.equals("No Selection") && !marker.getGenre().equals(styleFilter)) {
            return false;
        }

        switch (bounds) {
            case BOTH_BOUNDS:
                int upper = Integer.parseInt(upperBound.getText().toString());
                int lower = Integer.parseInt(lowerBound.getText().toString());
                if (marker.getYear() > upper || marker.getYear() < lower) {
                    return false;
                }
                break;
            case UPPER_ONLY:
                int upperB = Integer.parseInt(upperBound.getText().toString());
                if (marker.getYear() > upperB) {
                    return false;
                }
                break;
            case LOWER_ONLY:
                int lowerB = Integer.parseInt(lowerBound.getText().toString());
                if (marker.getYear() < lowerB){
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }
}
