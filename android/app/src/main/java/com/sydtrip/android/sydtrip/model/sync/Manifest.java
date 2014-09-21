package com.sydtrip.android.sydtrip.model.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Manifest {

    @SerializedName("format")
    private int mVersion;

    @SerializedName("files")
    private List<ManifestFile> mFiles;

    public int getVersion() {
        return mVersion;
    }

    public List<ManifestFile> getFiles() {
        return mFiles;
    }

    @Override
    public String toString() {
        return "Manifest{" +
                "version=" + mVersion +
                ", files=" + mFiles +
                '}';
    }
}
