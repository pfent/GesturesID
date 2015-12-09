package fent.de.tum.in.sensorprocessing.measurement;

public class SensorData {

    public final float[][] data;

    public SensorData(float[][] data) {
        this.data = data;
    }

    public int getDimension() {
        return data.length;
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
