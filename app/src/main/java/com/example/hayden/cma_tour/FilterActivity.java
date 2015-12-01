package com.example.hayden.cma_tour;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FilterActivity extends Activity {
    private Button mSubmitButton;

    private final String TAG = "CMA_MAP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // set up the button
        mSubmitButton = (Button) findViewById(R.id.Submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FINISHING FORMACTIVITY");
                setResult(RESULT_OK);
                finish();

            }
        });
    }
}
