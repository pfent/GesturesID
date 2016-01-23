package fent.de.tum.in.gesturesid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;

import fent.de.tum.in.gesturesid.fragments.EvaluationFinishedFragment;
import fent.de.tum.in.gesturesid.fragments.LoadingFragment;
import fent.de.tum.in.sensorprocessing.CachedDBManager;
import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureExtractor;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;
import fent.de.tum.in.sensorprocessing.featureextraction.PeakDetector;
import fent.de.tum.in.sensorprocessing.featureextraction.PhoneKeystrokeFeatureExtractor;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;
import fent.de.tum.in.sensorprocessing.preprocessing.ExponentialSmoother;
import fent.de.tum.in.sensorprocessing.preprocessing.Normalizer;
import fent.de.tum.in.sensorprocessing.preprocessing.Preprocessor;
import fent.de.tum.in.sensorprocessing.preprocessing.Selector;

public class EvaluationActivity extends FragmentActivity {

    private static final Preprocessor selector = new Selector(2),// Z-Axis
            normalizer = new Normalizer(),
            smoother = new ExponentialSmoother(0.5f);
    private static final FeatureExtractor extractor = new PhoneKeystrokeFeatureExtractor();
    private static final PeakDetector peakDetector = new PeakDetector(67, 1.5f);

    private final AsyncTask<Void, Void, Void> computeTask = new AsyncTask<Void, Void, Void>() {

        private long startTime;

        @Override
        protected Void doInBackground(Void... params) {
            startTime = System.currentTimeMillis();
            MeasurementManager manager = MeasurementManager.getInstance(getApplicationContext());
            CachedDBManager cache = CachedDBManager.getInstance(getApplicationContext());
            cache.clear();

            Log.d("debug", "Started background task");
            List<Long> users = manager.getAllUsers();

            for (long userID : users) {
                List<Long> measurements = manager.getMeasurementsForUser(userID);
                for (long measurementID : measurements) {
                    Log.d("debug", "Started crunching measurementID " + measurementID);
                    SensorData data = manager.getSensorData(measurementID);
                    SensorData selectedData = selector.preprocess(data);
                    SensorData normalizedData = normalizer.preprocess(selectedData);
                    cache.addNormalizedData(measurementID, normalizedData.data[0]);

                    final int[] tapLocations = peakDetector.setTimeSeriesData(normalizedData.data[0]).process();
                    cache.insertPeaks(measurementID, tapLocations);

                    FeatureVectors features = extractor.extractFeatures(normalizedData);

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            CachedDBManager cache = CachedDBManager.getInstance(getApplicationContext());
            Log.d("debug", "Finished background task");
            setComputationFinished(System.currentTimeMillis() - startTime,
                    cache.getDatabaseName());
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
                .add(R.id.fragment_container, LoadingFragment.newInstance())
                .commit();

        computeTask.execute();
    }

    private void setComputationFinished(long computeTime, String dbLocation) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EvaluationFinishedFragment.newInstance(computeTime, dbLocation))
                .commit();
    }
}
