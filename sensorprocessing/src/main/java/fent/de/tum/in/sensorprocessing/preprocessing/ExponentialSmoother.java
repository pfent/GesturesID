package fent.de.tum.in.sensorprocessing.preprocessing;


import fent.de.tum.in.sensorprocessing.measurement.SensorData;

/**
 * Implements an exponential filter to smoothen the SensorData and better localize the distinctive values
 */
public class ExponentialSmoother implements Preprocessor {

    private final float alpha;

    /**
     * @param alpha the exponential factor: in rage (0…1). Lower alpha means slower averaging
     */
    public ExponentialSmoother(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public SensorData preprocess(SensorData data) {
        for (final float[] dataRow : data.data) {
            final int dataRowSize = dataRow.length;

            float oldValue = dataRow[0];

            for (int i = 0; i < dataRowSize; i++) {
                final float value = dataRow[i];
                final float newValue = oldValue + alpha * (value - oldValue);
                oldValue = newValue;
                dataRow[i] = newValue;
            }
        }

        return data;
    }
}
