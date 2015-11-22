package fent.de.tum.in.gesturesid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileOutputStream;

import fent.de.tum.in.gesturesid.sensormeasurement.SensorData;

public class MeasurementActivity extends AppCompatActivity implements OnPatternReceivedListener {

    private SensorData sensorData;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private MeasurementActivityFragment inputFragment = MeasurementActivityFragment.newInstance();
    private SensorDisplayFragment displayFragment = SensorDisplayFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputFragment.setOnPatternReceivedListener(this);
        viewPager = (ViewPager) findViewById(R.id.vPager);
        pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return inputFragment;
                    case 1:
                        return displayFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
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
    public void OnPatternReceived(SensorData data) {
        sensorData = data;
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("csvdump.csv", Context.MODE_PRIVATE);
            outputStream.write(data.toCSV().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("CSVDUMP", e.toString());
        }
        displayFragment.displayData(data);
    }
}
