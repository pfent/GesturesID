package fent.de.tum.in.gesturesid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import fent.de.tum.in.gesturesid.fragments.LoadingFragment;
import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;
import fent.de.tum.in.sensorprocessing.preprocessing.ComposingPreprocessor;
import fent.de.tum.in.sensorprocessing.preprocessing.ExponentialSmoother;
import fent.de.tum.in.sensorprocessing.preprocessing.Normalizer;
import fent.de.tum.in.sensorprocessing.preprocessing.Preprocessor;
import fent.de.tum.in.sensorprocessing.preprocessing.Selector;

public class EvaluationActivity extends FragmentActivity {

    private final AsyncTask<Void, Void, Void> computeTask = new AsyncTask<Void, Void, Void>() {
        MeasurementManager manager = MeasurementManager.getInstance(EvaluationActivity.this);

        @Override
        protected Void doInBackground(Void... params) {
            List<Long> users = manager.getAllUsers();
            for (long userID : users) {
                List<Long> measurements = manager.getMeasurementsForUser(userID);
                for (long measurementID : measurements) {
                    SensorData data = manager.getSensorData(measurementID);

                    Preprocessor preprocessor = new ComposingPreprocessor(
                            new Selector(2), // Z-Axis
                            new Normalizer(),
                            new ExponentialSmoother(0.5f)
                    );

                    data = preprocessor.preprocess(data);

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        if (savedInstanceState != null) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new LoadingFragment())
                .commit();
    }
}
