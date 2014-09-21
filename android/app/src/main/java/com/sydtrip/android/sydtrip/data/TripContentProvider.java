package com.sydtrip.android.sydtrip.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.sydtrip.android.sydtrip.BuildConfig;
import com.sydtrip.android.sydtrip.model.sync.ManifestFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class TripContentProvider extends ContentProvider {

    private static final String AUTHORITY = BuildConfig.TRIP_CONTENT_PROVIDER_AUTHORITY;

    private ContentResolver mContentResolver;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static Map<ManifestFile.Type, TripDb> manifestFileToTripDb = new HashMap<>();

    static {
        for (int i = 0, len = ManifestFile.Type.values().length; i < len; i++) {
            for (int j = 0, jLen = Contract.TABLES.length; j < jLen; j++) {
                final String type = ManifestFile.Type.values()[i].name();
                final String table = Contract.TABLES[j].getName();

                sURIMatcher.addURI(AUTHORITY, type + "/" + table, (i * jLen) + j);
            }
        }
    }

    public static Uri getUri(ManifestFile.Type type, String tableName) {
        return new Uri.Builder()
                .scheme("content")
                .authority(AUTHORITY)
                .appendPath(type.name())
                .appendPath(tableName)
                .build();
    }

    @Override
    public boolean onCreate() {
        mContentResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String sel, String[] selArgs, String sort) {
        try {
            final int uriType = sURIMatcher.match(uri);

            final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(getTableFromType(uriType).getName());

            final TripDb db = getDatabaseFromType(uriType);

            final Cursor cursor = qb.query(db.getReadableDatabase(),
                    projection, sel, selArgs, null, null, sort, uri.getQueryParameter("limit"));

            if (cursor != null) {
                cursor.setNotificationUri(mContentResolver, uri);
            }

            return cursor;
        } catch (Exception e) {
            Timber.e(e, "Error querying database");
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return sURIMatcher.match(uri) == UriMatcher.NO_MATCH ?
                null : uri.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Can't call insert");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Can't call delete");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("Can't call update");
    }

    private DbTable getTableFromType(int type) {
        return Contract.TABLES[type % Contract.TABLES.length];
    }

    private TripDb getDatabaseFromType(int type) {
        final ManifestFile.Type manifestFileType
                = ManifestFile.Type.values()[type / Contract.TABLES.length];

        TripDb db = manifestFileToTripDb.get(manifestFileType);
        if (db == null) {
            db = new TripDb(getContext(), manifestFileType);
            manifestFileToTripDb.put(manifestFileType, db);
        }

        return db;
    }

    private static class TripDb extends SQLiteOpenHelper {

        private SQLiteDatabase db;
        private final Context context;
        private final ManifestFile.Type fileType;

        public TripDb(Context context, ManifestFile.Type fileType) {
            super(context, fileType.getDatabaseFileName(), null, 1);
            this.context = context;
            this.db = getReadableDatabase();
            this.fileType = fileType;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            throw new IllegalStateException("Can't write to this database");
        }

        @Override
        public SQLiteDatabase getReadableDatabase() {
            try {
                if (db != null && db.isOpen()) {
                    return db;
                }
                return SQLiteDatabase.openDatabase(getDatabasePath(),
                        null, SQLiteDatabase.OPEN_READONLY);
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        public synchronized void close() {
            if (db != null) {
                db.close();
                db = null;
            }
            super.close();
        }

        private String getDatabasePath() {
            return new File(context.getFilesDir(), fileType.getDatabaseFileName()).getAbsolutePath();
        }
    }

    public static class Contract {

        public static class TableNames {
            public static String DYNAMIC_TEXT = "dynamic_text";
            public static String STOPS = "stops";
            public static String ROUTES = "routes";
            public static String TRIPS = "trips";
            public static String STOP_TIMES = "stop_times";
        }

        public static DbTable[] TABLES = {
                DbTable.with(TableNames.DYNAMIC_TEXT)
                        .columns(Columns.ID,
                                Columns.VALUE)
                        .create(),
                DbTable.with(TableNames.STOPS)
                        .columns(Columns.ID,
                                Columns.CODE,
                                Columns.NAME,
                                Columns.LAT,
                                Columns.LNG,
                                Columns.TYPE,
                                Columns.PLATFORM_CODE)
                        .create(),
                DbTable.with(TableNames.ROUTES)
                        .columns(Columns.ID,
                                Columns.AGENCY_ID,
                                Columns.SHORT_NAME,
                                Columns.LONG_NAME,
                                Columns.COLOR)
                        .create(),
                DbTable.with(TableNames.TRIPS)
                        .columns(Columns.ID,
                                Columns.HEAD_SIGN,
                                Columns.DIRECTION,
                                Columns.BLOCK_ID,
                                Columns.WHEELCHAIR_ACCESS,
                                Columns.ROUTE_ID)
                        .create(),
                DbTable.with(TableNames.STOP_TIMES)
                        .columns(Columns.TRIP_ID,
                                Columns.STOP_ID,
                                Columns.SECONDS_SINCE_MIDNIGHT)
                        .create(),
        };

        public static class Columns {
            public static final DbField ID = new DbField("_id", "INTEGER", "PRIMARY KEY");
            public static final DbField VALUE = new DbField("value", "TEXT");
            public static final DbField AGENCY_ID = new DbField("agencyId", "INTEGER");
            public static final DbField SHORT_NAME = new DbField("shortName", "TEXT");
            public static final DbField LONG_NAME = new DbField("longName", "TEXT");
            public static final DbField COLOR = new DbField("color", "INTEGER");
            public static final DbField TRIP_ID = new DbField("tripId", "INTEGER");
            public static final DbField STOP_ID = new DbField("stopId", "INTEGER");
            public static final DbField SECONDS_SINCE_MIDNIGHT = new DbField("secondsSinceMidnight", "INTEGER");
            public static final DbField CODE = new DbField("code", "INTEGER");
            public static final DbField NAME = new DbField("name", "TEXT");
            public static final DbField LAT = new DbField("lat", "REAL");
            public static final DbField LNG = new DbField("lng", "REAL");
            public static final DbField TYPE = new DbField("type", "INTEGER");
            public static final DbField PLATFORM_CODE = new DbField("platformCode", "TEXT");
            public static final DbField HEAD_SIGN = new DbField("headSign", "TEXT");
            public static final DbField DIRECTION = new DbField("direction", "INTEGER");
            public static final DbField BLOCK_ID = new DbField("blockId", "TEXT");
            public static final DbField WHEELCHAIR_ACCESS = new DbField("wheelchairAccess", "INTEGER");
            public static final DbField ROUTE_ID = new DbField("routeId", "INTEGER");
        }
    }
}
