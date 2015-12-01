package com.example.hayden.cma_tour;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class DisplayInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String year = intent.getStringExtra("year");
        String genre = intent.getStringExtra("genre");

        String info = "Title: " + title + "\nArtist: " + artist + "\nYear: " + year + "\nGenre: " + genre;
        TextView textView = new TextView(this);
        textView.setTextSize(30);
        textView.setText(info);

        setContentView(textView);

    }

}
