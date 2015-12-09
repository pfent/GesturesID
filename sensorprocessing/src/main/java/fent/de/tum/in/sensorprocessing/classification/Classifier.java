package fent.de.tum.in.sensorprocessing.classification;

import fent.de.tum.in.sensorprocessing.classification.classificationDistances.ClassificationDistancer;
import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;

public abstract class Classifier {
    protected final FeatureVectors[][] categories;
    protected final ClassificationDistancer distancer;


    protected Classifier(FeatureVectors[][] categories, ClassificationDistancer distancer) {
        this.categories = categories;
        this.distancer = distancer;

    }

    public abstract int classify(FeatureVectors features);
}
