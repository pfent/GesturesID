package fent.de.tum.in.sensorprocessing;

import fent.de.tum.in.sensorprocessing.measurement.SensorData;

public interface OnPatternReceivedListener {

    void OnPatternReceived(SensorData data, long startTime, long endTime);

}
