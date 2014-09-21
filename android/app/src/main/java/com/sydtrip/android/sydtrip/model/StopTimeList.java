package com.sydtrip.android.sydtrip.model;

import java.util.LinkedList;

public class StopTimeList extends LinkedList<StopTime> {

    public boolean doesStopsAtStop(Stop stop) {
        if (stop != null) {
            for (StopTime time : this) {
                if (time.getStopId() == stop.getId()) {
                    return true;
                }
            }
        }

        return false;
    }
}
