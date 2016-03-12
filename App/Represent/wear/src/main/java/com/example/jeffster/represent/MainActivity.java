package com.example.jeffster.represent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import layout.CongressmanFragment;

public class MainActivity extends Activity {

    private JSONObject congressmenData;
    private final String[] NAMES = {"Hillary Clinton", "Bloopy Blurpingon", "Dom Perignon"};
    private final String[] AFFILIATIONS = {"Democrat", "Independent", "Republican"};
    private final int[] DRAWABLE_IDS = {R.drawable.hilary, R.drawable.perot, R.drawable.schumer};
    private GridViewPager pager;
    private SensorManager mSensorManager;

    private float mAccelPrev;
    private boolean firstSensorRead;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init the shakin'sensors
        mAccelPrev = 0.0f;
        firstSensorRead = true;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Bundle extras = getIntent().getExtras();
        try {
            congressmenData = new JSONObject(extras.getString("json_object"));
        } catch (JSONException e) {
            // ignored
        }

        pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new RepresentGridPagerAdapter(this, getFragmentManager(), congressmenData));
        pager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {
                //Don't need to do nothin'
            }

            @Override
            public void onPageSelected(int row, int col) {
                if (col > 0) {
                    Log.d("pageSelected", "help");
                    Intent intent = new Intent(getApplicationContext(),
                            WatchToPhoneService.class);
                    intent.putExtra("json_object", congressmenData.optJSONArray("results")
                            .optJSONObject(col - 1).toString());
                    startService(intent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Don't need to do nothin'
            }
        });
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            if (Math.abs(mAccelPrev - se.values[0]) > 2 && !firstSensorRead) {
                Log.d("SENSE", "currently in onSensorChanged");

                Intent intent = new Intent(getApplicationContext(), WatchToPhoneService.class);
                intent.putExtra("new_loc", "");
                startService(intent);
            }
            mAccelPrev = se.values[0];
            firstSensorRead = false;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    private class RepresentGridPagerAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;
        private List mRows;
        private JSONArray congressmenArray;

        public RepresentGridPagerAdapter(Context ctx, FragmentManager fm, JSONObject congressmenData) {
            super(fm);

            congressmenArray = congressmenData.optJSONArray("results");

            mContext = ctx;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public Drawable getBackgroundForPage(int row, int col) {
            return mContext.getResources().getDrawable(R.drawable.empty, null);

        }

        @Override
        public Fragment getFragment(int row, int col) {
            if (col == 0) {
                return ElectionFragment.newInstance(congressmenData.optString("state"),
                        congressmenData.optString("county"), congressmenData.optInt("red_percent"));
            } else {
                JSONObject congressman = congressmenArray.optJSONObject(col - 1);
                return CongressmanFragment.newInstance(congressman);
            }
        }

        @Override
        public int getColumnCount(int row) {
            return congressmenArray.length() + 1;
        }
    }


}
