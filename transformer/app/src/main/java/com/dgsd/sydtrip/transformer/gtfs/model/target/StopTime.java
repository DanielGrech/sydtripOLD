package com.dgsd.sydtrip.transformer.gtfs.model.target;

import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingStopTime;

public class StopTime {

    private final int stopId;

    private final int secondsSinceMidnight;

    public StopTime(GtfsStagingStopTime stopTime) {
        stopId = stopTime.getStopId();
        secondsSinceMidnight = stopTime.getArrivalTime();
    }

    public int getStopId() {
        return stopId;
    }

    public long getTime() {
        return secondsSinceMidnight;
    }


    @Override
    public String toString() {
        return "StopTime{" +
                "stopId=" + stopId +
                ", time=" + secondsSinceMidnight +
                '}';
    }
}
