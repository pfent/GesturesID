package fent.de.tum.in.sensorprocessing.featureextraction;

import java.util.ArrayList;
import java.util.List;

public class FeatureVectorsBuilder {
    private List<List<Float>> buildupData;

    public FeatureVectorsBuilder(int vectors) {
        this.buildupData = new ArrayList<>(vectors);

        for (int i = 0; i < vectors; i++) {
            this.buildupData.add(new ArrayList<Float>());
        }
    }

    public FeatureVectorsBuilder appendItemToVector(int vectorIndex, float item) {
        this.buildupData.get(vectorIndex).add(item);
        return this;
    }

    public FeatureVectors build() {
        List<float[]> result = new ArrayList<>(this.buildupData.size());

        for (List<Float> data : this.buildupData) {
            float[] vector = new float[data.size()];
            for (int i = 0; i < vector.length; i++) {
                vector[i] = data.get(i);
            }
            result.add(vector);
        }

        return new FeatureVectors(result);
    }
}