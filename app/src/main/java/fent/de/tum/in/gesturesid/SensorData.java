package fent.de.tum.in.gesturesid;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class SensorData {

    /**
     * SensorDataBuilder for building up SensorData Objects.
     * Example usage:
     * SensorData data = new SensorDataBuilder(new float[]{0,1,2})
     * .append(new float[]{3,4,5})
     * .toSensorData();
     */
    public class SensorDataBuilder {
        private static final int initialCapacity = 2048;
        List<List<Float>> buildupData;


        public SensorDataBuilder(float[] initialDataVector) {
            buildupData = new ArrayList<>(initialDataVector.length);
            for (float data : initialDataVector) {
                ArrayList<Float> dataRow = new ArrayList<Float>(initialCapacity);
                dataRow.add(data);
                buildupData.add(dataRow);
            }
        }

        /**
         * Append a new data vector to the data set
         *
         * @param dataVector the data vector to be added
         * @return the SensorDataBuilder
         * @throws IllegalArgumentException if the dataVector has a other size than initialy created
         */
        public SensorDataBuilder append(float[] dataVector) {
            if (dataVector.length != buildupData.size()) {
                throw new IllegalArgumentException();
            }

            for (int i = 0; i < dataVector.length; i++) {
                buildupData.get(i).add(dataVector[i]);
            }

            return this;
        }

        /**
         * Convert this Builder to a SensorData Object
         *
         * @return the SensorData built
         */
        public SensorData toSensorData() {
            final int vectorSize = buildupData.size();
            float[][] result = new float[vectorSize][];

            for (int i = 0; i < vectorSize; i++) {
                final List<Float> dataRow = buildupData.get(i);
                final int dataRowSize = dataRow.size();
                result[i] = new float[dataRowSize];

                for (int j = 0; j < dataRowSize; j++) {
                    result[i][j] = dataRow.get(j);
                }
            }

            return new SensorData(result);
        }
    }

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

            for(int j = 0; j < dataRowSize; j++) {
                dataPointRow[j] = new DataPoint(j, dataRow[j]);
            }

            // now set this dataPointRow to the appropriate View
            final GraphView view = views[i];
            view.removeAllSeries();
            view.addSeries(new LineGraphSeries<>(dataPointRow));
        }
    }
}
