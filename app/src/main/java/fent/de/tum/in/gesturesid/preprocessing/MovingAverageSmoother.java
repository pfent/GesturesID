package fent.de.tum.in.gesturesid.preprocessing;

import fent.de.tum.in.sensormeasurement.SensorData;

/**
 * Implements a simple moving average filter to smoothen the SensorData and better localize the
 * distinctive values
 */
public class MovingAverageSmoother implements Preprocessor {

    private final int windowSize;

    /**
     * @param windowSize the size of the window to smoothen the data. Keep smaller than the datasize
     */
    public MovingAverageSmoother(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public SensorData preprocess(SensorData data) {
        float[] window = new float[windowSize];
        for (final float[] dataRow : data.data) {
            if(dataRow.length < windowSize) {
                // Window size is too large smoothening does not make sense
                continue;
            }
            float sum = 0;
            // initially fill the window. Smoothing gradually gets better
            for (int i = 0; i < windowSize; i++) {
                window[i] = dataRow[i];
                sum += dataRow[i];
                dataRow[i] = sum / i;
            }
            // calculate the average of the last (windowSize) data points
            for (int i = windowSize; i < dataRow.length; i++) {
                final int wrappedPosition = i % windowSize;
                sum -= window[wrappedPosition];
                window[wrappedPosition] = dataRow[i];
                sum += dataRow[i];
                dataRow[i] = sum / windowSize;
            }
        }

        return data;
    }
}
