package fent.de.tum.in.gesturesid;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SensorData {

    private final float[][] data;

    public SensorData(float[][] data) {
        this.data = data;
        normalizeData(data);
        exponentiallySmoothData(data, 0.75f);
    }

    /**
     * Normalize the dataSet as difference from the mean
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
     * @param alpha the exponential factor: in rage (0,1). Lower alpha means slower averaging
     */
    private static void exponentiallySmoothData(float[][] dataSet, float alpha) {
        for (final float[] dataRow : dataSet) {
            final int dataRowSize = dataRow.length;

            float oldValue = dataRow[0];

            for (int i = 0; i < dataRowSize; i++) {
                final float value = dataRow[i];
                final float newValue = oldValue + alpha * (value + oldValue);
                oldValue = newValue;
                dataRow[i] = newValue;
            }
        }
    }

    public void displayData(GraphView[] views) {
        if (views.length != data.length) {
            throw new IllegalArgumentException();
        }

        final int vectorSize = data.length;

        for (int i = 0; i < vectorSize; i++) {
            final float[] dataRow = data[i];
            final DataPoint[] dataPointRow = new DataPoint[dataRow.length];
            final int dataRowSize = data[i].length;

            for (int j = 0; j < dataRowSize; j++) {
                dataPointRow[j] = new DataPoint(j, dataRow[j]);
            }

            // now set this dataPointRow to the appropriate View
            final GraphView view = views[i];
            view.removeAllSeries();
            view.addSeries(new LineGraphSeries<>(dataPointRow));
        }
    }
}
