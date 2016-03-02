package com.example.jeffster.represent;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FloatingActionButton loc = (FloatingActionButton) findViewById(R.id.loc);
        EditText zip = (EditText) findViewById(R.id.zip);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get location
                // open new activity
                startListCandidatesActivity(10000);
            }
        });

        zip.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                // lookup location
                // open new activity
                if (v.getText().length() != 5) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                                    "Please enter a valid ZIP", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                startListCandidatesActivity(Integer.parseInt(v.getText().toString()));
                return true;
            }
        });
    }

    private void startListCandidatesActivity(int zip) {
        //Start the watch notifier
        Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
        serviceIntent.putExtra("location_string", Integer.toString(zip));
        startService(serviceIntent);

        Intent intent = new Intent(this, ListCandidatesActivity.class);
        intent.putExtra("location", zip);
        startActivity(intent);
    }
}
