package fent.de.tum.in.sensorprocessing.featureextraction;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

/**
 * This class implements a FeatureExtrator for acceleration data gathered on a phone. The SensorData
 * should only contain appropriate data to detect Taps on the phone's display via peak datection.
 * Usually, the Z-Acceleration of the phone is a good input.
 */
public class PhoneKeystrokeFeatureExtractor implements FeatureExtractor {

    public final int
            TAP_INTENSITY = 0,
            INTERVAL = 1;
    public final int FEATURE_NUMBER = 2;

    private final int SensorDataRow = 0;


    private PeakDetector peakDetector = new PeakDetector(10, 1.5f); //TODO: compare values

    public PhoneKeystrokeFeatureExtractor() {
        //TODO: set PeakDetector paramters according to the mean time for a datapoint
    }

    @Override
    public FeatureVectors extractFeatures(SensorData data) {
        final int[] tapLocations = peakDetector.setTimeSeriesData(data.data[SensorDataRow]).process();
        Integer lastTap = null;

        FeatureVectorsBuilder builder = new FeatureVectorsBuilder(FEATURE_NUMBER);

        for(int i = 0; i < tapLocations.length; i++) {

            builder.appendItemToVector(TAP_INTENSITY, data.data[SensorDataRow][tapLocations[i]]);

            if(lastTap != null) {
                builder.appendItemToVector(INTERVAL, tapLocations[i] - lastTap);
            }

            lastTap = tapLocations[i];
        }


        return null;
    }
}
