package com.example.hayden.cma_tour;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewMarkerActivity extends Activity {
    private Button mSubmitMarkerButton;
    private EditText mTitle, mArtist, mYear, mGenre, mFloor;
    private String title, artist, year, genre, lat, lng, floor, filename;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private  void checkFieldsForEmptyValues(){
        mSubmitMarkerButton = (Button) findViewById(R.id.SubmitMarker);

        String s1 = mTitle.getText().toString();
        String s2 = mArtist.getText().toString();
        String s3 = mYear.getText().toString();
        String s4 = mGenre.getText().toString();
        String s5 = mFloor.getText().toString();

        if((s1 != null && !s1.isEmpty()) &&
                (s2 != null && !s2.isEmpty()) &&
                (s3 != null && !s3.isEmpty()) &&
                (s4 != null && !s4.isEmpty()) &&
                (s5 != null && !s5.isEmpty())){
            mSubmitMarkerButton.setEnabled(true);
        }
        else{
            mSubmitMarkerButton.setEnabled(false);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);


        mTitle  = (EditText) findViewById(R.id.editTitleText);
        mArtist = (EditText) findViewById(R.id.editArtistText);
        mYear   = (EditText) findViewById(R.id.editYearText);
        mGenre  = (EditText) findViewById(R.id.editGenreText);
        mFloor  = (EditText) findViewById(R.id.editFloorText);

        mTitle.addTextChangedListener(textWatcher);
        mArtist.addTextChangedListener(textWatcher);
        mYear.addTextChangedListener(textWatcher);
        mGenre.addTextChangedListener(textWatcher);
        mFloor.addTextChangedListener(textWatcher);

        checkFieldsForEmptyValues();

        //Set up Button
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
