package com.sydtrip.android.sydtrip.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sydtrip.android.sydtrip.model.Stop;
import com.sydtrip.android.sydtrip.model.StopTime;
import com.sydtrip.android.sydtrip.model.StopTimeList;
import com.sydtrip.android.sydtrip.util.DaoUtils;
import com.sydtrip.android.sydtrip.util.ProviderUtils;

import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.Columns;
import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.TableNames;

public class RoutingEngine implements IRoutingEngine {

    private final Context mContext;

    public RoutingEngine(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public StopTimeList getStopTimesAt(Stop stop) {
        final Uri uri = TripContentProvider.getUri(null, TableNames.STOP_TIMES);

        final Cursor cursor = ProviderUtils.query(uri)
                .where(Columns.STOP_ID + " = ?", String.valueOf(stop.getId()))
                .sort(Columns.SECONDS_SINCE_MIDNIGHT + " ASC")
                .cursor(mContext);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                final StopTimeList retval = new StopTimeList();
                do {
                    retval.add(DaoUtils.build(StopTime.class, cursor));
                } while(cursor.moveToNext());
                return retval;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return new StopTimeList();
    }
}
