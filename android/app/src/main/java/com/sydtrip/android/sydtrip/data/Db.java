package com.sydtrip.android.sydtrip.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import timber.log.Timber;

import java.lang.reflect.Modifier;

public class Db extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DB_NAME = "SydTrip.db";

    private static Db sInstance;

    public static final Object[] LOCK = new Object[0];

    public static Db get(Context c) {
        if (sInstance == null)
            sInstance = new Db(c);

        return sInstance;
    }

    protected Db(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        runForEachTable(new TableRunnable() {
            @Override
            public void run(final DbTable table) {
                db.execSQL(table.getCreateSql());
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: Support db upgrade...
        throw new IllegalStateException("We havent implemented db upgrades yet!");
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        synchronized (LOCK) {
            return super.getReadableDatabase();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        synchronized (LOCK) {
            return super.getWritableDatabase();
        }
    }

    /**
     * Execute a given against each table in the database
     *
     * @param runnable The task to perform
     */
    private void runForEachTable(TableRunnable runnable) {
        java.lang.reflect.Field[] declaredFields = Db.Table.class.getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(DbTable.class)) {
                try {
                    runnable.run((DbTable) field.get(null));
                } catch (IllegalAccessException e) {
                    Timber.e(e, "Error executing table runnable: " + field.getName());
                }
            }
        }
    }

    /**
     * Encapsulates a task to be run against a table
     */
    private interface TableRunnable {
        /**
         * Execute the request task
         *
         * @param table The table to execute the task on
         */
        public void run(DbTable table);
    }

    /**
     * Database fields used in the app
     */
    public static class Field {
        private Field() {
        }

        public static final DbField ID = new DbField("_id", "integer", "primary key");
    }

    /**
     * Application database tables
     */
    public static class Table {
        private Table() {
        }
    }
}