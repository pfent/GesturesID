package fent.de.tum.in.sensorprocessing.measurement;

import java.util.ArrayList;
import java.util.List;

/**
 * SensorDataBuilder for building up SensorData Objects.
 * Example usage:
 * SensorData data = new SensorDataBuilder(new float[]{0,1,2})
 * .append(new float[]{3,4,5})
 * .toSensorData();
 */
public class SensorDataBuilder {
    private static final int initialCapacity = 2048;
    private List<List<Float>> buildupData;
    private List<Long> timeStamps;

    public SensorDataBuilder(int dimension) {
        this(new float[dimension], System.nanoTime());
    }

    public SensorDataBuilder(float[] initialDataVector, long timestamp) {
        buildupData = new ArrayList<>(initialDataVector.length);
        timeStamps = new ArrayList<>(initialCapacity);
        timeStamps.add(timestamp);
        for (float data : initialDataVector) {
            ArrayList<Float> dataRow = new ArrayList<>(initialCapacity);
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
    public SensorDataBuilder append(float[] dataVector, long timestamp) {
        if (dataVector.length != buildupData.size()) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < dataVector.length; i++) {
            buildupData.get(i).add(dataVector[i]);
        }

        timeStamps.add(timestamp);

        return this;
    }

    public void clear() {
        for (List<Float> line : buildupData) {
            line.clear();
        }
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

        long[] timestampResult = new long[timeStamps.size()];
        for(int i = 0; i < timeStamps.size(); i++) {
            timestampResult[i] = timeStamps.get(i);
        }

        return new SensorData(result, timestampResult);
    }
}