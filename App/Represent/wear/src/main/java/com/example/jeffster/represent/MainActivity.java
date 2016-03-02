package com.example.jeffster.represent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;

import java.util.List;

import layout.CongressmanFragment;

public class MainActivity extends Activity {

    private TextView mTextView;
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
        int location = Integer.parseInt(extras.getString("location_string"));

        pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new RepresentGridPagerAdapter(this, getFragmentManager(), location));
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
                    intent.putExtra("candidate_name", NAMES[col - 1]);
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
                //currently just switches between two locations
                int temp_loc = Math.random() > 0.5 ? 10000 : 11111;

                Log.d("SENSE", "currently in onSensorChanged");

                Intent intent = new Intent(getApplicationContext(), WatchToPhoneService.class);
                intent.putExtra("location_string", Integer.toString(temp_loc));
                startService(intent);

                pager.setAdapter(new RepresentGridPagerAdapter(getApplicationContext(),
                        getFragmentManager(), temp_loc));
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
        private int loc;

        public RepresentGridPagerAdapter(Context ctx, FragmentManager fm, int location) {
            super(fm);
            mContext = ctx;
            loc = location;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public Drawable getBackgroundForPage(int row, int col) {
            if (col == 0) {
                return mContext.getResources().getDrawable(R.drawable.empty, null);
            } else {
                return mContext.getResources().getDrawable(DRAWABLE_IDS[col - 1], null);
            }
        }

        @Override
        public Fragment getFragment(int row, int col) {
            if (col == 0) {
                if (loc == 11111) {
                    return ElectionFragment.newInstance("New York", "The Bronx", 39);
                } else {
                    return ElectionFragment.newInstance("California", "Solano", 45);
                }
            } else {
                return CongressmanFragment.newInstance(NAMES[col - 1], AFFILIATIONS[col - 1]);
            }
        }

        @Override
        public int getColumnCount(int row) {
            return NAMES.length + 1;
        }
    }
}
