package fent.de.tum.in.sensorprocessing.featureextraction;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public interface FeatureExtractor {
    FeatureVectors extractFeatures(SensorData data);
}
