package fent.de.tum.in.gesturesid;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;

public class SensorData {

    private final float[][] data;

    public SensorData(float[][] data) {
        this.data = data;
        exponentiallySmoothData(data, 0.5f);
        normalizeData(data);
    }

    /**
     * Normalize the dataSet to get a mean of 0
     */
    private static void normalizeData(float[][] dataSet) {
        for (final float[] dataRow : dataSet) {
            final int dataRowSize = dataRow.length;

            double sum = 0;
            for (float value : dataRow) {
                sum += value;
            }

            final double mean = sum / dataRowSize;
            for (int j = 0; j < dataRowSize; j++) {
                dataRow[j] -= mean;
            }
        }
    }

    /**
     * Smooth the dataSet to better localize the distinctive values
     *
     * @param alpha the exponential factor: in rage (0…1). Lower alpha means slower averaging
     */
    private static void exponentiallySmoothData(float[][] dataSet, float alpha) {
        for (final float[] dataRow : dataSet) {
            final int dataRowSize = dataRow.length;

            float oldValue = dataRow[0];

            for (int i = 0; i < dataRowSize; i++) {
                final float value = dataRow[i];
                final float newValue = oldValue + alpha * (value - oldValue);
                oldValue = newValue;
                dataRow[i] = newValue;
            }
        }
    }

    private static void movingAverageSmoothData(float[][] dataSet, int windowSize) {
        float[] window = new float[windowSize];
        for (final float[] dataRow : dataSet) {
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
            for (int j = Math.max(1, i - windowSize); i < (Math.min(dTWMatrixWidth, i + windowSize)); i++) {
                final float cost = Math.abs(first[i - 1] - second[j - 1]); // Euclidean distance
                dTWMatrix[i][j] = cost +
                        Math.min(dTWMatrix[i - 1][j], // insert
                                Math.min(dTWMatrix[i][j - 1], // delete
                                        dTWMatrix[i - 1][j - 1])); // match
            }
        }

        return dTWMatrix[dTWMatrixHeight][dTWMatrixWidth];
    }

    public int getDimension() {
        return data.length;
    }

    public void displayData(GraphView[] views) {
        if (views.length != data.length) {
            throw new IllegalArgumentException();
        }

        final int vectorSize = data.length;

        for (int i = 0; i < vectorSize; i++) {
            displayData(views[i], i);
        }
    }

    public void displayData(GraphView view, int index) {
        if (index >= data.length) {
            throw new IllegalArgumentException();
        }

        final float[] dataRow = data[index];
        final DataPoint[] dataPointRow = new DataPoint[dataRow.length];
        final int dataRowSize = data[index].length;

        for (int j = 0; j < dataRowSize; j++) {
            dataPointRow[j] = new DataPoint(j, dataRow[j]);
        }

        // now set this dataPointRow to the appropriate View
        view.removeAllSeries();
        view.addSeries(new LineGraphSeries<>(dataPointRow));
    }

    public String toCSV() {
        StringBuilder builder = new StringBuilder();
        for (float[] line : data) {
            for (float f : line) {
                builder.append(f).append(';');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
