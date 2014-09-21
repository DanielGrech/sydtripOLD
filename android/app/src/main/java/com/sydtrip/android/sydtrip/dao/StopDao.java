package com.sydtrip.android.sydtrip.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.sydtrip.android.sydtrip.data.CursorColumnMap;
import com.sydtrip.android.sydtrip.model.Stop;
import com.sydtrip.android.sydtrip.util.ContentValuesBuilder;

import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.Columns;

public class StopDao implements IDao<Stop> {

    @Override
    public Stop build(Cursor cursor) {
        final CursorColumnMap cc = new CursorColumnMap(cursor);
        final Stop stop = new Stop();

        stop.setId(cc.getInt(Columns.ID, -1));
        stop.setCode(cc.getString(Columns.CODE));
        stop.setName(cc.getString(Columns.NAME));
        stop.setLat(cc.getFloat(Columns.LAT, 0f));
        stop.setLng(cc.getFloat(Columns.LNG, 0f));
        stop.setType(cc.getInt(Columns.TYPE, -1));
        stop.setPlatformCode(cc.getString(Columns.PLATFORM_CODE));
        stop.setParentId(cc.getInt(Columns.PARENT_ID, -1));

        return stop;
    }

    @Override
    public ContentValues convert(Stop stop) {
        return new ContentValuesBuilder()
                .put(Columns.ID, stop.getId())
                .put(Columns.CODE, stop.getCode())
                .put(Columns.NAME, stop.getName())
                .put(Columns.LAT, stop.getLat())
                .put(Columns.LNG, stop.getLng())
                .put(Columns.TYPE, stop.getType())
                .put(Columns.PLATFORM_CODE, stop.getPlatformCode())
                .put(Columns.PARENT_ID, stop.getParentId())
                .build();
    }
}
