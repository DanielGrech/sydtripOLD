package com.sydtrip.android.sydtrip.data;

import com.sydtrip.android.sydtrip.model.Stop;
import com.sydtrip.android.sydtrip.model.StopTimeList;

public interface IRoutingEngine {

    public StopTimeList getStopTimesAt(Stop stop);
}
