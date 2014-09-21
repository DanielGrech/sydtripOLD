package com.sydtrip.android.sydtrip.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.sydtrip.android.sydtrip.data.CursorColumnMap;
import com.sydtrip.android.sydtrip.model.StopTime;
import com.sydtrip.android.sydtrip.util.ContentValuesBuilder;

import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.Columns;

public class StopTimeDao implements IDao<StopTime> {

    @Override
    public StopTime build(Cursor cursor) {
        final CursorColumnMap cc = new CursorColumnMap(cursor);

        final StopTime st = new StopTime();

        st.setTripId(cc.getInt(Columns.TRIP_ID, -1));
        st.setStopId(cc.getInt(Columns.STOP_ID, -1));
        st.setSecondsSinceMidnight(cc.getInt(Columns.SECONDS_SINCE_MIDNIGHT, -1));

        return st;
    }

    @Override
    public ContentValues convert(StopTime stopTime) {
        return new ContentValuesBuilder()
                .put(Columns.TRIP_ID, stopTime.getTripId())
                .put(Columns.STOP_ID, stopTime.getStopId())
                .put(Columns.SECONDS_SINCE_MIDNIGHT, stopTime.getSecondsSinceMidnight())
                .build();
    }
}
