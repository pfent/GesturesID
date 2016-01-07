package fent.de.tum.in.gesturesid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.primitives.Chars;
import com.google.common.primitives.Longs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fent.de.tum.in.gesturesid.fragments.EndFragment;
import fent.de.tum.in.gesturesid.fragments.MeasurementActivityFragment;
import fent.de.tum.in.gesturesid.fragments.NameInputFragment;
import fent.de.tum.in.gesturesid.fragments.SensorDisplayFragment;
import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public class MeasurementActivity extends AppCompatActivity implements OnPatternReceivedListener, NameInputFragment.OnNameInputListener, TextWatcher {

    private SensorData sensorData;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private static final int INPUT_FRAGMENTS = 10;
    private MeasurementActivityFragment[] inputFragments = new MeasurementActivityFragment[INPUT_FRAGMENTS];
    private MeasurementManager measurementManager = MeasurementManager.getInstance(this);
    private NameInputFragment nameFragment = NameInputFragment.newInstance();
    private EndFragment endFragment = EndFragment.newInstance();
    private static final int TOTAL_FRAGMENTS = INPUT_FRAGMENTS + 2;
    private long userID = -1;
    private List<Character> enteredCharacters = new ArrayList<>();
    private List<Long> characterTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i = 0; i < inputFragments.length; i++) {
            inputFragments[i] = MeasurementActivityFragment.newInstance();
        }
        viewPager = (ViewPager) findViewById(R.id.vPager);
        pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return nameFragment;
                    case TOTAL_FRAGMENTS - 1:
                        return endFragment;
                    default:
                        return inputFragments[position - 1];
                }
            }

            @Override
            public int getCount() {
                return TOTAL_FRAGMENTS;
            }
        };
        viewPager.setAdapter(pagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() < TOTAL_FRAGMENTS - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
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

        if (userID < 0) {
            this.userID = measurementManager.createUser("noname");
        }

        long measurementID = measurementManager.createMeasurement(userID);
        measurementManager.addMeasurementData(measurementID, data.data, data.timestamps);
        measurementManager.addKeyStrokes(measurementID,
                Chars.toArray(enteredCharacters),
                Longs.toArray(characterTimes));

        enteredCharacters.clear();

        measurementManager.copyDbToSdCard();
    }

    @Override
    public void onNameInput(String name) {
        this.userID = measurementManager.createUser(name);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("onTextChanged called");
        if(start >= s.length()) {
            return;
        }
        characterTimes.add(System.nanoTime());
        enteredCharacters.add(s.charAt(start));
    }

    @Override
    public void afterTextChanged(Editable s) {
        // NOP
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // NOP
    }
}
