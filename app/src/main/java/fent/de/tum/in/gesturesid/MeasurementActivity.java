package fent.de.tum.in.gesturesid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import fent.de.tum.in.gesturesid.fragments.MeasurementActivityFragment;
import fent.de.tum.in.gesturesid.fragments.NameInputFragment;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public class MeasurementActivity extends AppCompatActivity implements OnPatternReceivedListener, NameInputFragment.OnNameInputListener {

    private SensorData sensorData;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private MeasurementActivityFragment inputFragment;
    private SensorDisplayFragment displayFragment = SensorDisplayFragment.newInstance();
    private MeasurementManager measurementManager = MeasurementManager.getInstance(this);
    private NameInputFragment nameFragment = new NameInputFragment().newInstance();
    private long userID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputFragment = MeasurementActivityFragment.newInstance();
        viewPager = (ViewPager) findViewById(R.id.vPager);
        pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return nameFragment;
                    case 1:
                        return inputFragment;
                    case 2:
                        return displayFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        viewPager.setAdapter(pagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measurement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnPatternReceived(SensorData data, long startTime, long endTime) {
        sensorData = data;

        if (userID < 0) {
            throw new IllegalStateException("userID needs to be set first");
        }

        long measurementID = measurementManager.createMeasurement(userID);
        measurementManager.addMeasurementData(measurementID, startTime, endTime, data.data);

        //displayFragment.displayData(data);
    }

    @Override
    public void onNameInput(String name) {
        this.userID = measurementManager.createUser(name);
    }
}
