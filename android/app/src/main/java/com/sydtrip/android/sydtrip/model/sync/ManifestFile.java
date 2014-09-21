package com.sydtrip.android.sydtrip.model.sync;

import com.google.gson.annotations.SerializedName;

public class ManifestFile {

    public static String DATABASE_FILE_SUFFIX = ".db";
    public static String DATABASE_FILE_IN_SYNC_SUFFIX = ".BAK";

    public static enum Type {
        BUS, RAIL, FERRY, LIGHTRAIL;

        public String getDatabaseFileName() {
            return name() + DATABASE_FILE_SUFFIX;
        }

        public String getDatabaseFileSyncName() {
            return getDatabaseFileName() + DATABASE_FILE_IN_SYNC_SUFFIX;
        }
    }

    @SerializedName("name")
    private String mName;

    @SerializedName("type")
    private Type mType;

    public String getName() {
        return mName;
    }

    public Type getType() {
        return mType;
    }

    public String getDatabaseFileName() {
        return mType.getDatabaseFileName();
    }

    public String getDatabaseFileSyncName() {
        return mType.getDatabaseFileSyncName();
    }

    @Override
    public String toString() {
        return "ManifestFile{" +
                "name='" + mName + '\'' +
                ", type=" + mType +
                '}';
    }
}
