package com.example.jeffster.represent;

import android.app.Activity;
import android.os.Bundle;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class WelcomeActivity extends Activity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "vqZMhJj2p1lGI0ptr5htgesEM";
    private static final String TWITTER_SECRET = "4LZrLuXMdUCkqx0PXHccAk9G8EDI4nN1QddePeEOzw28GLxwJ3";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_welcome);
    }


}
