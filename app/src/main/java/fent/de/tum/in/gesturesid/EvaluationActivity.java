package fent.de.tum.in.gesturesid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import fent.de.tum.in.gesturesid.fragments.EvaluationFinishedFragment;
import fent.de.tum.in.gesturesid.fragments.LoadingFragment;
import fent.de.tum.in.sensorprocessing.CachedDBManager;
import fent.de.tum.in.sensorprocessing.MeasurementManager;
import fent.de.tum.in.sensorprocessing.classification.Classifier;
import fent.de.tum.in.sensorprocessing.classification.classificationDistances.dTWDistancer;
import fent.de.tum.in.sensorprocessing.classification.kNNClassifier;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureExtractor;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;
import fent.de.tum.in.sensorprocessing.featureextraction.PhoneKeystrokeFeatureExtractor;
import fent.de.tum.in.sensorprocessing.measurement.SensorData;
import fent.de.tum.in.sensorprocessing.preprocessing.Normalizer;
import fent.de.tum.in.sensorprocessing.preprocessing.Preprocessor;
import fent.de.tum.in.sensorprocessing.preprocessing.Selector;

public class EvaluationActivity extends FragmentActivity {

    private static final Preprocessor selector = new Selector(2),// Z-Axis
            normalizer = new Normalizer();
    private static final FeatureExtractor extractor = new PhoneKeystrokeFeatureExtractor();
    private final AsyncTask<Void, Void, Void> computeTask = new AsyncTask<Void, Void, Void>() {

        private long startTime;
        private String result = "";

        @Override
        protected Void doInBackground(Void... params) {
            startTime = System.currentTimeMillis();
            MeasurementManager manager = MeasurementManager.getInstance(getApplicationContext());
            CachedDBManager cache = CachedDBManager.getInstance(getApplicationContext());
            cache.clear();

            List<Long> users = manager.getAllUsers();

            FeatureVectors[][] categories = new FeatureVectors[users.size()][];

            for (int i = 0; i < users.size(); i++) {
                List<Long> measurements = manager.getMeasurementsForUser(users.get(i));
                categories[i] = new FeatureVectors[measurements.size() - 1];
                for (int j = 0; j < measurements.size() - 1; j++) {
                    final long measurementID = measurements.get(j);
                    SensorData data = manager.getSensorData(measurementID);
                    SensorData selectedData = selector.preprocess(data);
                    SensorData normalizedData = normalizer.preprocess(selectedData);
                    categories[i][j] = extractor.extractFeatures(normalizedData);

                }
            }

            Classifier classifier = new kNNClassifier(categories, new dTWDistancer(3), 7);

            for (int i = 0; i < users.size(); i++) {
                List<Long> measurements = manager.getMeasurementsForUser(users.get(i));

                final long measurementID = measurements.get(measurements.size() - 1);
                SensorData data = manager.getSensorData(measurementID);
                SensorData selectedData = selector.preprocess(data);
                SensorData normalizedData = normalizer.preprocess(selectedData);
                FeatureVectors featureVectors = extractor.extractFeatures(normalizedData);

                int category = classifier.classify(featureVectors);

                result += String.format("user %d was identified as: %d\n", users.get(i), users.get(category));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setComputationFinished(System.currentTimeMillis() - startTime, result);
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

    private void setComputationFinished(long computeTime, String userIdentificationString) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EvaluationFinishedFragment.newInstance(computeTime, userIdentificationString))
                .commit();
    }
}
