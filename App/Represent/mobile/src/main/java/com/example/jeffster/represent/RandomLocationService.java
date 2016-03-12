package com.example.jeffster.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

public class RandomLocationService extends Service {

    private Random mRandom;

    public RandomLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRandom = new Random();
        super.onCreate();
        validZipHelper();

        return START_STICKY;
    }

    private void validZipHelper() {

        String reqType = "address=";
        String zipCandidate = Integer.toString(mRandom.nextInt(100000));
        String url = " https://maps.googleapis.com/maps/api/geocode/json?" + reqType + zipCandidate
                + "&region=us&key=" + getString(R.string.google_key);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                            validZipHelper();
                        } else {
                            JSONArray addressComponents = response.optJSONArray("results")
                                    .optJSONObject(0)
                                    .optJSONArray("address_components");
                            for (int i = 0; i < addressComponents.length(); i++) {
                                JSONObject addressComponent = addressComponents.optJSONObject(i);
                                JSONArray types = addressComponent.optJSONArray("types");
                                if (types.optString(0).equalsIgnoreCase("country")) {
                                    if (!addressComponent.optString("short_name")
                                            .equalsIgnoreCase("US")) {
                                        validZipHelper();
                                    }
                                }
                            }
                            JSONObject results = response.optJSONArray("results").optJSONObject(0);
                            JSONObject loc = results.optJSONObject("geometry").optJSONObject("location");
                            getDataHelper(loc.optDouble("lat"), loc.optDouble("lng"));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void getDataHelper(final double lat, final double lon) {
        String apiKey = "&apikey=" + getString(R.string.sunlight_key);
        String location = "latitude=" + lat + "&longitude=" + lon;

        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?" +
                location + apiKey;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            Intent intent = new Intent(RandomLocationService.this,
                                    ListCandidatesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("json_object", response.toString());
                            intent.putExtra("zip", -1);
                            intent.putExtra("lat_lon", new double[] {lat, lon});
                            startActivity(intent);
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
