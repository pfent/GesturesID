package fent.de.tum.in.sensorprocessing.measurement;

public class SensorData {

    public final float[][] data;
    public final long[] timestamps;

    public SensorData(float[][] data, long[] timestamps) {
        this.data = data;
        this.timestamps = timestamps;
    }

    public int getDimension() {
        return data.length;
    }
}
