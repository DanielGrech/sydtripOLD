package com.sydtrip.android.sydtrip.model.sync;

import com.google.gson.annotations.SerializedName;

public class ManifestFile {

    public static enum Type {
        BUS, RAIL, FERRY, LIGHTRAIL
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

    @Override
    public String toString() {
        return "ManifestFile{" +
                "name='" + mName + '\'' +
                ", type=" + mType +
                '}';
    }
}
