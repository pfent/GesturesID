package fent.de.tum.in.gesturesid;

import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ActionPage;
import android.view.View;

import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.OnPatternReceivedListener;
import fent.de.tum.in.sensorprocessing.measurement.PatternRecorder;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public class MeasurementActivity extends WearableActivity implements OnPatternReceivedListener {
    public static final String USER_ID = "user_id";

    private ActionPage mActionPage;
    private boolean isRecording = false;

    private PatternRecorder recorder;
    private MeasurementManager manager;
    private Long currentUserID;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Perform action on click
            if (!isRecording) {
                isRecording = true;
                mActionPage.setColor(Color.RED);
                mActionPage.setText(getText(R.string.stop_measurement));
                recorder.startListening();
                return;
            }
            isRecording = false;
            mActionPage.setColor(Color.GREEN);
            mActionPage.setText(getText(R.string.start_measurement));
            recorder.stopListening();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        setAmbientEnabled();

        mActionPage = (ActionPage) findViewById(R.id.actionpage);
        mActionPage.setOnClickListener(onClickListener);

        recorder = new PatternRecorder(this, Sensor.TYPE_ACCELEROMETER, this);
        manager = MeasurementManager.getInstance(this);

        long userID = getIntent().getLongExtra(USER_ID, -1l);
        if (userID > 0) {
            currentUserID = userID;
        }

    }

    @Override
    public void OnPatternReceived(SensorData data) {
        if (currentUserID == null) {
            currentUserID = manager.createUser("wearDummy");
        }
        long measurementID = manager.createMeasurement(currentUserID);
        manager.addMeasurementData(measurementID, data.data, data.timestamps);
        manager.copyDbToSdCard();
    }
}
