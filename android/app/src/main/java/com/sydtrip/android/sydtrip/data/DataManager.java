package com.sydtrip.android.sydtrip.data;

import android.content.Context;

import com.sydtrip.android.sydtrip.model.sync.ManifestFile;

import java.io.File;

public class DataManager {

    private final Context mContext;

    public DataManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean hasData(ManifestFile.Type type) {
        if (type == null) {
            return false;
        }

        return new File(mContext.getFilesDir(), type.getDatabaseFileName()).exists();
    }

    public boolean isCurrentlySyncing(ManifestFile.Type type) {
        if (type == null) {
            return false;
        }

        return new File(mContext.getFilesDir(), type.getDatabaseFileSyncName()).exists();
    }
}
