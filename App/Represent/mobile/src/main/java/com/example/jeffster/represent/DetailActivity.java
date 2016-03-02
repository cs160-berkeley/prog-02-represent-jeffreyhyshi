package com.example.jeffster.represent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Bundle extras = getIntent().getExtras();
        String congressmanName = extras.getString("congressman_name");

        collapsingToolbar.setTitle(congressmanName);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = getLayoutInflater();

        LinearLayout committees = (LinearLayout) findViewById(R.id.detail_committees);
        for (int i = 0; i < 5; i++) {
            View view = inflater.inflate(R.layout.committee_item, committees, false);
            TextView committeeName = (TextView) view.findViewById(R.id.name);
            committeeName.setText(congressmanName + "'s Committee On Legal Definitions of Purple " + i);
            committees.addView(view);
        }

        LinearLayout bills = (LinearLayout) findViewById(R.id.detail_bills);
        for (int i = 0; i < 5; i++) {
            View view = inflater.inflate(R.layout.bill_card, bills, false);
            TextView billName = (TextView) view.findViewById(R.id.name);
            TextView billDate = (TextView) view.findViewById(R.id.date);
            TextView billDescription = (TextView) view.findViewById(R.id.description);
            billName.setText("HR" + (i+100));
            billDate.setText(Integer.toString(i + 2000));
            billDescription.setText(congressmanName + "'s Act, Part " + i);
            bills.addView(view);
        }

    }

}
