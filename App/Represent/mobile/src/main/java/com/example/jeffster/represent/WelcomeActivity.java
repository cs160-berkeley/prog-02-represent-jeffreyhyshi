package com.example.jeffster.represent;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;

    private ProgressBar prog;

    private double[] lon_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        lon_lat = new double[2];

        FloatingActionButton loc = (FloatingActionButton) findViewById(R.id.loc);
        EditText zip = (EditText) findViewById(R.id.zip);
        prog = (ProgressBar) findViewById(R.id.horizontal_progress);
        prog.setVisibility(View.GONE);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prog.setVisibility(View.VISIBLE);
                Log.d("location", "location should be set to " + lon_lat[0] + " " + lon_lat[1]);
                startListCandidatesActivity(-1, lon_lat[0], lon_lat[1]);
            }
        });

        zip.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                // lookup location
                // open new activity
                prog.setVisibility(View.VISIBLE);
                if (v.getText().length() != 5) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please enter a valid ZIP", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                startListCandidatesActivity(Integer.parseInt(v.getText().toString()), 0, 0);
                return true;
            }
        });

        GPSHandler gps = new GPSHandler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(gps)
                .addOnConnectionFailedListener(gps)
                .build();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    // To be called with zip=-1 if we are looking up by latitude and longitude
    private void startListCandidatesActivity(final int zip, final double lat, final double lon) {
        String apiKey = "&apikey=" + getString(R.string.sunlight_key);
        String location = zip == -1 ?
                "latitude=" + lat + "&longitude=" + lon :
                "zip=" + zip;
        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?" +
                location + apiKey;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        prog.setVisibility(View.GONE);
                        if (response.optJSONArray("results").length() == 0) {
                            Toast.makeText(getApplicationContext(), "Invalid zip",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(WelcomeActivity.this,
                                    ListCandidatesActivity.class);
                            intent.putExtra("json_object", response.toString());
                            intent.putExtra("zip", zip);
                            intent.putExtra("lat_lon", new double[] {lat, lon});
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error retrieving data",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }


    private class GPSHandler implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener {

        LocationRequest mLocationRequest;

        public GPSHandler() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        @Override
        public void onConnected(Bundle bundle) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult res) {
                    final Status status = res.getStatus();
                    final LocationSettingsStates loc = res.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        WelcomeActivity.this,
                                        1);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest,
                        (LocationListener) this);
            } catch (SecurityException e) {
                // Ignored.
            }
        }

        @Override
        public void onConnectionSuspended(int i) {}

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connResult) {}

        @Override
        public void onLocationChanged(Location location) {
            lon_lat[0] = location.getLatitude();
            lon_lat[1] = location.getLongitude();
            Log.d("hello", "from the other side");
        }

    }
}
