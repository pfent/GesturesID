package fent.de.tum.in.sensorprocessing.preprocessing;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public interface Preprocessor {
    SensorData preprocess(SensorData data);
}
