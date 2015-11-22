package fent.de.tum.in.sensormeasurement;

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


    public SensorDataBuilder(int dimension) {
        this(new float[dimension]);
    }

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

        return new SensorData(result);
    }
}