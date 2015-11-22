package fent.de.tum.in.gesturesid.preprocessing;

import fent.de.tum.in.sensormeasurement.SensorData;

public interface Preprocessor {
    SensorData preprocess(SensorData data);
}
