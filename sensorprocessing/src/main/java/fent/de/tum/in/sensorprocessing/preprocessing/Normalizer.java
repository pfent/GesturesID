package fent.de.tum.in.sensorprocessing.preprocessing;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

/**
 * Implements a normalizer to set the mean value of the SensorData to 0.
 */
public class Normalizer implements Preprocessor {
    @Override
    public SensorData preprocess(SensorData data) {
        for (final float[] dataRow : data.data) {
            float sum = 0;
            for (float datapoint : dataRow) {
                sum += datapoint;
            }

            final float mean = sum / dataRow.length;
            for (int i = 0; i < dataRow.length; i++) {
                dataRow[i] -= mean;
            }
        }

        return data;
    }
}
