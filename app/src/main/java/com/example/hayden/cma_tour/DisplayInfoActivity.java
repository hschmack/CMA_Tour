package com.example.hayden.cma_tour;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // layout/activity_display_marker_info.xml
        setContentView(R.layout.activity_display_marker_info);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String year = intent.getStringExtra("year");
        String genre = intent.getStringExtra("genre");
        Uri imgPath = Uri.parse(intent.getStringExtra("uri"));
        Bitmap img = BitmapFactory.decodeFile(imgPath.getPath());

        String info = "Title: " + title + "\nArtist: " + artist + "\nYear: " + year + "\nGenre: " + genre;

        TextView textView = (TextView) findViewById(R.id.marker_info);
        ImageView imgView = (ImageView) findViewById(R.id.marker_pic);

        textView.setTextSize(30);
        textView.setText(info);

        imgView.setImageBitmap(img);

        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(false);      // Disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // Remove the left caret
        }
        return true;
    }


}
