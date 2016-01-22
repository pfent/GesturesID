package fent.de.tum.in.sensorprocessing.preprocessing;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

/**
 * Implements a Preprocessor, that selects a single dimension of the SensorData
 */
public class Selector implements Preprocessor {

    final int index;

    public Selector(int index) {
        this.index = index;
    }

    @Override
    public SensorData preprocess(SensorData data) {
        if (index >= data.getDimension()) {
            throw new IllegalArgumentException(String.format("index %s >= datadimension %s", index, data.getDimension()));
        }

        return new SensorData(new float[][]{data.data[index]}, data.timestamps);
    }
}
