package com.sydtrip.android.sydtrip.data.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import com.sydtrip.android.sydtrip.data.TripContentProvider;
import com.sydtrip.android.sydtrip.model.Stop;
import com.sydtrip.android.sydtrip.model.sync.ManifestFile;
import com.sydtrip.android.sydtrip.util.DaoUtils;
import com.sydtrip.android.sydtrip.util.ProviderUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract;
import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.Columns;
import static com.sydtrip.android.sydtrip.data.TripContentProvider.Contract.TABLES;

public class StopLoader extends AsyncLoader<List<Stop>> {

    public static final Uri URI = Uri.parse("content://stop_loader");

    private final ManifestFile.Type mType;

    public StopLoader(Context context, ManifestFile.Type type) {
        super(context);
        mType = type;
    }

    @Override
    protected Uri getContentUri() {
        return URI;
    }

    @Override
    public List<Stop> loadInBackground() {
        final Uri uri = TripContentProvider.getUri(mType, Contract.TableNames.STOPS);

        Cursor cursor = null;
        try {
            cursor = ProviderUtils.query(uri)
                    .select(
                            Contract.TableNames.STOPS + "." + Columns.ID,
                            Columns.VALUE + " AS " + Columns.NAME,
                            Columns.CODE.getName(),
                            Columns.LAT.getName(),
                            Columns.LNG.getName(),
                            Columns.TYPE.getName(),
                            Columns.PLATFORM_CODE.getName(),
                            Columns.PARENT_ID.getName()
                    )
                    .where(Columns.PARENT_ID + " IS NULL")
                    .sort(Columns.NAME + " ASC")
                    .cursor(getContext());
            if (cursor != null && cursor.moveToFirst()) {
                final List<Stop> stops = new ArrayList<>(cursor.getCount());

                do {
                    stops.add(DaoUtils.build(Stop.class, cursor));
                } while (cursor.moveToNext());

                return stops;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }
}
