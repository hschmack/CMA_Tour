package com.example.hayden.cma_tour;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewMarkerActivity extends Activity {
    private Button mSubmitMarkerButton;
    private EditText mTitle, mArtist, mYear, mGenre, mFloor;
    private String title, artist, year, genre, lat, lng, floor, filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);


        mTitle  = (EditText) findViewById(R.id.editTitleText);
        mArtist = (EditText) findViewById(R.id.editArtistText);
        mYear   = (EditText) findViewById(R.id.editYearText);
        mGenre  = (EditText) findViewById(R.id.editGenreText);
        mFloor  = (EditText) findViewById(R.id.editFloorText);

        //Set up Button
        mSubmitMarkerButton = (Button) findViewById(R.id.SubmitMarker);
        mSubmitMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CMA_MAP", "FINISHING MARKER SUBMISSION");
                newMarkerOnSubmit();
                setResult(RESULT_OK);
                finish();

            }
        });
    }

    private void newMarkerOnSubmit() {
        title = mTitle.getText().toString();
        artist = mArtist.getText().toString();
        year = mYear.getText().toString();
        genre = mGenre.getText().toString();
        floor = mFloor.getText().toString();
        filename = MapsActivity.IMG_FOLDER + title;

        Art_Marker marker = new Art_Marker(title,
                artist,
                Integer.parseInt(year),
                genre,
//                MapsActivity.mCurrentLocation.getLatitude(),
//                MapsActivity.mCurrentLocation.getLongitude(),
                41.508858,
                -81.611819,
                Integer.parseInt(floor),
                filename,
                MapsActivity.mMap);
        MapsActivity.allMarkers.add(marker);
    }

}
