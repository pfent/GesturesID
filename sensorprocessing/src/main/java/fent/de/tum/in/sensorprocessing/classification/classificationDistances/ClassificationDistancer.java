package fent.de.tum.in.sensorprocessing.classification.classificationDistances;

import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;

public interface ClassificationDistancer {

    float compare(FeatureVectors lhs, FeatureVectors rhs);
}
