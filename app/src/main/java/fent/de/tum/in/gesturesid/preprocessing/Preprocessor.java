package fent.de.tum.in.gesturesid.preprocessing;

import fent.de.tum.in.gesturesid.sensormeasurement.SensorData;

public interface Preprocessor {
    SensorData preprocess(SensorData data);
}
