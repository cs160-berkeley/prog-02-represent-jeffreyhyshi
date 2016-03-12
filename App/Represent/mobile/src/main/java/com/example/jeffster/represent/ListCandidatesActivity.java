package com.example.jeffster.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import java.io.InputStream;

import io.fabric.sdk.android.Fabric;
import retrofit.http.GET;
import retrofit.http.Query;

public class ListCandidatesActivity extends AppCompatActivity {

    private LayoutInflater inflater;

    private LinearLayout mainLayout;
    private LinearLayout senators;
    private LinearLayout representatives;

    private String TWITTER_KEY;
    private String TWITTER_SECRET;

    //Reused repeatedly for setting images
    private int numCongressmen;
    private int numDisplayed = 0;

    private int zip;
    private double[] lat_lon;

    private JSONArray congressmenWithTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TWITTER_KEY = getString(R.string.twitter_key);
        TWITTER_SECRET = getString(R.string.twitter_secret);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));
        setContentView(R.layout.activity_list_candidates);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inflater = getLayoutInflater();
        mainLayout = (LinearLayout) this.findViewById(android.R.id.content)
                .findViewById(R.id.list_candidates_layout);

        senators = (LinearLayout) inflater.inflate(R.layout.senators, mainLayout, false);
        representatives = (LinearLayout) inflater.inflate(R.layout.representatives, mainLayout, false);

        congressmenWithTwitter = new JSONArray();

        Bundle extras = getIntent().getExtras();
        JSONObject response;
        try {
            response = new JSONObject(extras.getString("json_object"));
        } catch (JSONException e) {
            response = new JSONObject();
        }
        zip = extras.getInt("zip");
        lat_lon = extras.getDoubleArray("lat_lon");

        handleData(response);
    }

    private void handleData(JSONObject response) {
        final JSONObject res = response;
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> result) {
                try {
                    JSONArray congressmen = res.getJSONArray("results");
                    numCongressmen = congressmen.length();
                    for (int i = 0; i < numCongressmen; i++) {
                        JSONObject congressman = (JSONObject) congressmen.get(i);
                        LinearLayout layoutToUse = senators;
                        String location = congressman.optString("state", "");
                        if (congressman.getString("chamber").equalsIgnoreCase("house")) {
                            String district = congressman.getString("district").equals("0") ?
                                    "at large" :
                                    (congressman.getString("district") + "th District");
                            layoutToUse = representatives;
                            location = location + ", " + district;
                        }
                        String fullName = congressman.optString("first_name", "") + " " +
                                congressman.optString("last_name", "");

                        if (congressman.optString("twitter_id").equals("null")) {
                            addCongressmanCard(layoutToUse,
                                    fullName,
                                    congressman.optString("party", ""),
                                    location,
                                    null,
                                    congressman.optString("oc_email", ""),
                                    congressman.optString("website", ""),
                                    null,
                                    congressman.optString("bioguide_id", ""),
                                    congressman
                            );
                        } else {
                            twitterAddCongressmanCard(congressman, result.data, layoutToUse, location);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error parsing data",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("twitter", "can't connect to Twitter");
            }
        });
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

    private void twitterAddCongressmanCard(JSONObject c, AppSession a, LinearLayout l, String loc) {
        final JSONObject congressman = c;
        final LinearLayout layoutToUse = l;
        final String location = loc;
        MyTwitterApiClient twitterApiClient = new MyTwitterApiClient(a);
        twitterApiClient.getUsersService().show(congressman.optString("twitter_id", ""),
                new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        User user = userResult.data;
                        String twitterImageUrl = user.profileImageUrl.replace("_normal", "");
                        String latestTweetText = user.status.text;
                        String fullName = congressman.optString("first_name", "") + " " +
                                congressman.optString("last_name", "");

                        addCongressmanCard(layoutToUse,
                                fullName,
                                congressman.optString("party", ""),
                                location,
                                latestTweetText,
                                congressman.optString("oc_email", ""),
                                congressman.optString("website", ""),
                                twitterImageUrl,
                                congressman.optString("bioguide_id", ""),
                                congressman
                        );
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(getApplicationContext(), "Error retrieving Tweet",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startDetailActivity(String jsonObject) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("json_object", jsonObject);
        startActivity(intent);
    }

    private void addCongressmanCard(LinearLayout layoutToUse, final String name, final String affiliation,
                                    final String location, String tweet, final String email, final String website,
                                    final String twitterImageUrl, final String bioguideId,
                                    JSONObject congressman) {
        try {
            congressman.put("image_url", twitterImageUrl);
        } catch (JSONException e) { /* next */ }
        final String jsonString = congressman.toString();
        View view = inflater.inflate(R.layout.congressman_card, layoutToUse, false);

        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView affiliationView = (TextView) view.findViewById(R.id.affiliation);
        TextView locationView = (TextView) view.findViewById(R.id.location);
        TextView tweetView = (TextView) view.findViewById(R.id.tweet);
        TextView emailView = (TextView) view.findViewById(R.id.email);
        TextView websiteView = (TextView) view.findViewById(R.id.website);
        ImageView pic = (ImageView) view.findViewById(R.id.pic);
        Button infoButton = (Button) view.findViewById(R.id.button);

        nameView.setText(name);
        affiliationView.setText(affiliation);
        int circle = affiliation.equalsIgnoreCase("R") ?
                R.drawable.circle_red :
                (affiliation.equalsIgnoreCase("D") ? R.drawable.circle_blue : R.drawable.circle_grey);
        affiliationView.setBackground(ResourcesCompat.getDrawable(getResources(), circle, null));
        locationView.setText(location);
        if (tweet != null) {
            tweetView.setText(Html.fromHtml(tweet));
        }
        emailView.setText(email);
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        websiteView.setText(website);
        websiteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(website));
                startActivity(browserIntent);
            }
        });

        if (twitterImageUrl != null) {
            // Get the ImageLoader through singleton class.
            ImageLoader mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
            mImageLoader.get(twitterImageUrl, ImageLoader.getImageListener(pic,
                    R.drawable.empty, R.drawable.empty));
        } else {
            pic.setImageResource(R.drawable.broken_image);
        }

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailActivity(jsonString);
            }
        });

        congressmenWithTwitter.put(congressman);

        layoutToUse.addView(view);
        numDisplayed++;
        if (numDisplayed == numCongressmen) {
            final ProgressBar spinner = (ProgressBar) mainLayout.findViewById(R.id.progressBar);

            String reqType = zip == -1 ? "latlng=" : "address=";
            String locNumbers = zip == -1 ? "" + lat_lon[0] + "," + lat_lon[1] : "" + zip;
            String url = " https://maps.googleapis.com/maps/api/geocode/json?" + reqType + locNumbers
                    + "&region=us&key=" + getString(R.string.google_key);


            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray addressComponents = response.optJSONArray("results")
                                    .optJSONObject(0)
                                    .optJSONArray("address_components");
                            String county = "";
                            String state = "";
                            String stateLong="";
                            for (int i = 0; i < addressComponents.length(); i++) {
                                JSONObject addressComponent = addressComponents.optJSONObject(i);
                                JSONArray types = addressComponent.optJSONArray("types");
                                if (types.optString(0).equalsIgnoreCase("administrative_area_level_1")) {
                                    state = addressComponent.optString("short_name");
                                    stateLong = addressComponent.optString("long_name");
                                }
                                if (types.optString(0).equalsIgnoreCase("administrative_area_level_2")) {
                                    county = addressComponent.optString("long_name")
                                            .replace(" County", "");
                                }
                            }

                            JSONObject voteData;
                            try {
                                InputStream stream = getAssets().open("election_results_2012.json");
                                int size = stream.available();
                                byte[] buffer = new byte[size];
                                stream.read(buffer);
                                stream.close();
                                voteData = new JSONObject(new String(buffer, "UTF-8"));
                            } catch (Exception e) {
                                voteData = new JSONObject();
                            }

                            int romVote;
                            if (voteData.optJSONObject(state) != null) {
                                if (county.equalsIgnoreCase("")) {
                                    romVote = voteData.optJSONObject(state).optInt(stateLong);
                                    county = "At large";
                                } else {
                                    romVote = voteData.optJSONObject(state).optInt(county);
                                }
                            } else {
                                // Cool, I like that the data doesn't include 50 states
                                // Very cool, very swag i love it
                                romVote = 50;
                            }

                            Intent serviceIntent = new Intent(ListCandidatesActivity.this,
                                    PhoneToWatchService.class);
                            JSONObject toWatch = new JSONObject();
                            try {
                                toWatch.put("results", congressmenWithTwitter);
                                toWatch.put("red_percent", romVote);
                                toWatch.put("state", state);
                                toWatch.put("county", county);
                            } catch (JSONException e) {
                                /*ignored*/
                            }
                            serviceIntent.putExtra("json_object", toWatch.toString());
                            startService(serviceIntent);

                            spinner.setVisibility(View.GONE);

                            mainLayout.addView(senators);
                            mainLayout.addView(representatives);
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
    }

    class MyTwitterApiClient extends TwitterApiClient {
        public MyTwitterApiClient(AppSession session) {
            super(session);
        }

        public UsersService getUsersService() {
            return getService(UsersService.class);
        }
    }

    interface UsersService {
        @GET("/1.1/users/show.json")
        void show(@Query("screen_name") String screenName,
                  Callback<User> cb);
    }

}
