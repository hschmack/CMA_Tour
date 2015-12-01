package com.example.hayden.cma_tour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewMarkerActivity extends Activity {
    private Button mSubmitMarkerButton;
    private EditText mTitle, mArtist, mYear, mGenre, mFloor;
    private String title, artist, year, genre, lat, lng, floor, filename, csvEntry;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri tmpPath;
    private Art_Marker marker;

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
                Intent intent = new Intent();
                intent.putExtra("CSV_ENTRY", csvEntry);
                setResult(RESULT_OK, intent);
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
        filename = MapsActivity.IMG_FOLDER + "/" + title +"_"+artist;
        dispatchTakePictureIntent();

        marker = new Art_Marker(title,
                artist,
                Integer.parseInt(year),
                genre,
                MapsActivity.mCurrentLocation.getLatitude(),
                MapsActivity.mCurrentLocation.getLongitude(),
//                41.508858,
//                -81.611819,
                Integer.parseInt(floor),
                tmpPath.toString(),
                MapsActivity.mMap);

        MapsActivity.allMarkers.add(marker);
        csvEntry = marker.getCSVEntry();
    }

    /**
     * Taken from http://developer.android.com/training/camera/photobasics.html
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                Log.d("ERROR", "Error creating the image file");
            }
            if (photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                tmpPath = Uri.fromFile(photoFile);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Create the image file that is created on dispatchTakePictureIntent
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = title + "_" + artist;

        File image = File.createTempFile(imageFileName, ".jpg", MapsActivity.IMG_FOLDER);

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Write to CSV

//            Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(tmpPath.getPath()), 96, 96);
//            marker.addToMapWithImg(imageBitmap);
        }
    }
}
