package com.example.jeffster.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 * Modified by jshi for obvious reasons
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String CHANGE_DETAIL_VIEW = "/change_detail_view";
    private static final String CHANGE_LIST_VIEW = "/change_list_view";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(CHANGE_DETAIL_VIEW) ) {
            // Value contains whichever candidate we've got over there
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //Start a new activity
            Intent intent = new Intent(this, DetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("json_object", value);
            startActivity(intent);
        } else if ( messageEvent.getPath().equalsIgnoreCase(CHANGE_LIST_VIEW) ) {
            Intent intent = new Intent(this, RandomLocationService.class);
            startService(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
