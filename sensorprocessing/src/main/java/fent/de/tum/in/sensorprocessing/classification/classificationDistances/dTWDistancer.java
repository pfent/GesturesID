package fent.de.tum.in.sensorprocessing.classification.classificationDistances;

import java.util.Arrays;

import fent.de.tum.in.sensorprocessing.featureextraction.FeatureVectors;

public class dTWDistancer implements ClassificationDistancer {

    final Integer windowSize;

    public dTWDistancer() {
        windowSize = null;
    }

    public dTWDistancer(int windowsSize) {
        this.windowSize = windowsSize;
    }

    @Override
    public float compare(FeatureVectors lhs, FeatureVectors rhs) {
        float distance = 0;
        final int size = lhs.data.size();
        if(windowSize == null) {
            for(int i = 0; i < size; i++) {
                final float tmp = dTWDistance(lhs.data.get(i), rhs.data.get(i));
                distance += tmp * tmp;
            }
        } else {
            for(int i = 0; i < size; i++) {
                final float tmp = dTWDistance(lhs.data.get(i), rhs.data.get(i), windowSize);
                distance += tmp * tmp;
            }
        }

        return (float) Math.sqrt(distance);
    }

    private static float dTWDistance(float[] first, float[] second) {
        final int dTWMatrixHeight = first.length + 1;
        final int dTWMatrixWidth = second.length + 1;
        float[][] dTWMatrix = new float[dTWMatrixHeight][dTWMatrixWidth];

        // Fill the matrix' outermost values with starting values
        for (int i = 1; i < dTWMatrixHeight; i++) {
            dTWMatrix[i][0] = Float.POSITIVE_INFINITY;
        }
        for (int i = 1; i < dTWMatrixWidth; i++) {
            dTWMatrix[0][i] = Float.POSITIVE_INFINITY;
        }
        dTWMatrix[0][0] = 0;

        for (int i = 1; i < dTWMatrixHeight; i++) {
            for (int j = 1; j < dTWMatrixWidth; j++) {
                final float cost = Math.abs(first[i - 1] - second[j - 1]); // Euclidean distance
                dTWMatrix[i][j] = cost +
                        Math.min(dTWMatrix[i - 1][j], // insert
                                Math.min(dTWMatrix[i][j - 1], // delete
                                        dTWMatrix[i - 1][j - 1])); // match
            }
        }

        return dTWMatrix[dTWMatrixHeight][dTWMatrixWidth];
    }

    private static float dTWDistance(float[] first, float[] second, int desiredWindowSize) {
        final int dTWMatrixHeight = first.length + 1;
        final int dTWMatrixWidth = second.length + 1;
        float[][] dTWMatrix = new float[dTWMatrixHeight][dTWMatrixWidth];

        // Window size must at last be the array's size difference (+1)
        final int windowSize = Math.max(desiredWindowSize, Math.abs(dTWMatrixHeight - dTWMatrixWidth));

        for (float[] dTWRow : dTWMatrix) {
            Arrays.fill(dTWRow, Float.POSITIVE_INFINITY);
        }
        dTWMatrix[0][0] = 0;

        for (int i = 1; i < dTWMatrixHeight; i++) {
            for (int j = Math.max(1, i - windowSize); j < (Math.min(dTWMatrixWidth, j + windowSize)); j++) {
                final float cost = Math.abs(first[i - 1] - second[j - 1]); // Euclidean distance
                dTWMatrix[i][j] = cost +
                        Math.min(dTWMatrix[i - 1][j], // insert
                                Math.min(dTWMatrix[i][j - 1], // delete
                                        dTWMatrix[i - 1][j - 1])); // match
            }
        }

        return dTWMatrix[dTWMatrixHeight][dTWMatrixWidth];
    }
}
