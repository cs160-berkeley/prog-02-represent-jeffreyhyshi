package com.example.jeffster.represent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailActivity extends AppCompatActivity {

    private JSONArray committeeArray;
    private JSONArray billsArray;
    private LayoutInflater inflater;
    private LinearLayout committees;
    private LinearLayout bills;
    private String congressmanName;
    private JSONObject congressman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Bundle extras = getIntent().getExtras();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            congressman = new JSONObject(extras.getString("json_object"));
        } catch (JSONException e) {
            congressman = new JSONObject();
        }

        congressmanName = congressman.optString("first_name") + " " +
                congressman.optString("last_name");
        String bioguideId = congressman.optString("bioguide_id");
        String imageUrl = congressman.optString("image_url");

        collapsingToolbar.setTitle(congressmanName);
        setSupportActionBar(toolbar);

        inflater = getLayoutInflater();
        committees = (LinearLayout) findViewById(R.id.detail_committees);
        bills = (LinearLayout) findViewById(R.id.detail_bills);

        ImageView imageView = (ImageView) findViewById(R.id.detail_pic);
        TextView affiliationView = (TextView) findViewById(R.id.detail_affiliation);
        TextView locationView = (TextView) findViewById(R.id.detail_location);
        TextView termView = (TextView) findViewById(R.id.detail_term);

        String location = congressman.optString("state","");
        if (congressman.optString("chamber").equalsIgnoreCase("house")) {
            String district = congressman.optString("district").equals("0") ?
                    "at large" :
                    (congressman.optString("district") + "th District");
            location = location + ", " + district;
        }
        if (imageUrl != null && imageUrl.equals("")) {
            imageView.setImageResource(R.drawable.broken_image);
        } else {
            // Get the ImageLoader through singleton class.
            ImageLoader mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
            mImageLoader.get(imageUrl, ImageLoader.getImageListener(imageView,
                    R.drawable.empty, R.drawable.empty));
        }
        String affiliation = congressman.optString("party");
        affiliationView.setText(affiliation);
        int circle = affiliation.equalsIgnoreCase("R") ?
                R.drawable.circle_red :
                (affiliation.equalsIgnoreCase("D") ? R.drawable.circle_blue : R.drawable.circle_grey);
        affiliationView.setBackground(ResourcesCompat.getDrawable(getResources(), circle, null));
        locationView.setText(location);
        String term = congressman.optString("term_start").substring(0, 4) + " - " +
                congressman.optString("term_end").substring(0, 4);
        termView.setText(term);

        String url = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + bioguideId +
                    "&apikey=" + getString(R.string.sunlight_key);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sunlight", "committee responded");
                            committeeArray = response.getJSONArray("results");
                            populateCommittees();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error retrieving data",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error retrieving data",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        url = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bioguideId +
                "&apikey=" + getString(R.string.sunlight_key);
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sunlight", "committee responded");
                            billsArray = response.getJSONArray("results");
                            populateBills();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error retrieving data",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error retrieving data",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateCommittees() {
        for (int i = 0; i < committeeArray.length(); i++) {
            View view = inflater.inflate(R.layout.committee_item, committees, false);
            TextView committeeName = (TextView) view.findViewById(R.id.name);
            committeeName.setText(committeeArray.optJSONObject(i).optString("name"));
            committees.addView(view);
        }
        ProgressBar progress = (ProgressBar) findViewById(R.id.committees_progress);
        progress.setVisibility(View.GONE);
    }

    private void populateBills() {
        int maxBills = billsArray.length() > 10 ? 10 : billsArray.length();
        for (int i = 0; i < maxBills; i++) {
            View view = inflater.inflate(R.layout.bill_card, bills, false);
            TextView billName = (TextView) view.findViewById(R.id.name);
            TextView billDate = (TextView) view.findViewById(R.id.date);
            TextView billDescription = (TextView) view.findViewById(R.id.description);
            billName.setText(billsArray.optJSONObject(i).optString("bill_id").toUpperCase());
            billDate.setText(billsArray.optJSONObject(i).optString("introduced_on"));
            if (billsArray.optJSONObject(i).optString("short_title").equals("null")) {
                billDescription.setText(billsArray.optJSONObject(i).optString("official_title"));
            } else {
                billDescription.setText(billsArray.optJSONObject(i).optString("short_title"));
            }
            bills.addView(view);
        }
        ProgressBar progress = (ProgressBar) findViewById(R.id.bills_progress);
        progress.setVisibility(View.GONE);
    }
}
