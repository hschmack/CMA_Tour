package com.example.hayden.cma_tour;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewMarkerActivity extends Activity {
    private Button mSubmitMarkerButton;
    private EditText mEdit;
    private String title, artist, year, genre, lat, lng, floor, filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        mEdit = (EditText) findViewById(R.id.editTitleText);
        title = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editArtistText);
        artist = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editYearText);
        year = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editGenreText);
        genre = mEdit.getText().toString();
        mEdit = (EditText) findViewById(R.id.editFloorText);
        floor = mEdit.getText().toString();
        filename = "DCIM/CMA_PHOTOS/" + title;

        //Set up Button
        mSubmitMarkerButton = (Button) findViewById(R.id.SubmitMarker);
        mSubmitMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Marker Form", "FINISHING MARKER SUBMISSION");
                Art_Marker marker = new Art_Marker(title,
                                                    artist,
                                                    Integer.parseInt(year),
                                                    genre,
                                                    MapsActivity.mCurrentLocation.getLatitude(),
                                                    MapsActivity.mCurrentLocation.getLongitude(),
                                                    Integer.parseInt(floor),
                                                    filename,
                                                    MapsActivity.mMap);
                MapsActivity.allMarkers.add(marker);
                setResult(RESULT_OK);
                finish();

            }
        });
    }

}
