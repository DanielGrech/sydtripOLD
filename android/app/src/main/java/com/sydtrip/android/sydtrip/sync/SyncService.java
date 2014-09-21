package com.sydtrip.android.sydtrip.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;
import com.sydtrip.android.sydtrip.BuildConfig;
import com.sydtrip.android.sydtrip.STApp;
import com.sydtrip.android.sydtrip.model.sync.Manifest;
import com.sydtrip.android.sydtrip.model.sync.ManifestFile;
import com.sydtrip.android.sydtrip.util.CompressionUtils;
import com.sydtrip.android.sydtrip.util.PrefUtils;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

public class SyncService extends Service {

    private static SyncAdapter sSyncAdapter;

    private static final Object LOCK = new Object();

    @Inject
    PrefUtils prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (LOCK) {
            if (sSyncAdapter == null) {
                final STApp app = (STApp) getApplicationContext();
                app.inject(this);
                sSyncAdapter = new SyncAdapter(app, prefs);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    private static class SyncAdapter extends AbstractThreadedSyncAdapter {

        private static final String KEY_LAST_SYNC_TIME = "_last_sync_time";

        private static final int THREAD_POOL_SIZE = 4;

        private static final SimpleDateFormat IFMODIFIEDSINCE_FORMAT =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

        private final PrefUtils prefs;

        private final STApp app;

        public SyncAdapter(Context context, PrefUtils prefs) {
            super(context, true);
            this.prefs = prefs;
            this.app = (STApp) context.getApplicationContext();
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                                  ContentProviderClient provider, SyncResult syncResult) {
            Timber.w("onPerformSync()");
            final long lastSyncTime = prefs.get(KEY_LAST_SYNC_TIME, -1L);
            final long minPeriodBetween = TimeUnit.SECONDS.toMillis(BuildConfig.SYNC_MIN_PERIOD_BETWEEN);
            final long currentTime = getCurrentTimeInSeconds();
            final boolean hasDatabaseFiles = hasDatabaseFiles();

            if (lastSyncTime > 0 && app.isAppInForeground()) {
                if (hasDatabaseFiles) {
                    Timber.d("App is in foreground. Not syncing");
                    return;
                }
            }

            if (!hasDatabaseFiles || (currentTime - lastSyncTime) > minPeriodBetween) {
                final boolean newData = runSync(hasDatabaseFiles ? lastSyncTime : -1);
                if (newData) {
                    prefs.set(KEY_LAST_SYNC_TIME, getCurrentTimeInSeconds());
                }
            } else {
                Timber.d("Sync ran %s seconds ago. Skipping", (currentTime - lastSyncTime));
            }
        }

        private boolean runSync(final long lastSyncTime) {
            try {
                final Manifest manifest = getManifest(lastSyncTime);
                Timber.d("Got manifest: %s", manifest);

                if (manifest != null && manifest.getFiles() != null) {
                    final ExecutorService threadPool
                            = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
                    for (final ManifestFile file : manifest.getFiles()) {
                        threadPool.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final File existingXzFile
                                            = new File(getContext().getFilesDir(), file.getName());
                                    if (existingXzFile.exists()) {
                                        existingXzFile.delete();
                                    }

                                    final File xzFile = getManifestFile(file, lastSyncTime);
                                    if (xzFile == null || !xzFile.exists()) {
                                        throw new IllegalStateException(
                                                "No xz file found for: " + file);
                                    }

                                    Timber.d("Got manifest file: [%s] [%s]", file, xzFile);

                                    final File dbBackupFile = new File(xzFile.getParentFile(),
                                            file.getDatabaseFileSyncName());

                                    final File dbFile = CompressionUtils.decompress(xzFile,
                                            dbBackupFile.getAbsolutePath());
                                    if (dbFile == null || !dbFile.exists()) {
                                        throw new IllegalStateException(
                                                "No db file found for " + file);
                                    }

                                    replaceFile(dbFile);

                                    Timber.d("Compression finished for: [%s]", file);
                                } catch (Throwable t) {
                                    Timber.e(t, "Error running sync");
                                }
                            }
                        });
                    }
                    threadPool.shutdown();
                    threadPool.awaitTermination(30, TimeUnit.MINUTES);

                    return true;
                }
            } catch (ExecutionException | InterruptedException e) {
                Timber.e(e, "Error running sync");
            }

            return false;
        }

        private void replaceFile(File file) {
            final File existingFile = new File(file.getAbsolutePath()
                    .replace(ManifestFile.DATABASE_FILE_IN_SYNC_SUFFIX, ""));
            if (existingFile.exists()) {
                existingFile.delete();
            }
            file.renameTo(existingFile);
        }

        private Manifest getManifest(long lastSyncTime)
                throws ExecutionException, InterruptedException {
            final String ifModifiedSince = lastSyncTime < 0 ? null :
                    IFMODIFIEDSINCE_FORMAT.format(new Date(TimeUnit.SECONDS.toMillis(lastSyncTime)));

            return Ion.with(getContext())
                    .load(BuildConfig.SYNC_BASE_URL + BuildConfig.SYNC_FILE_MANIFEST)
                    .addHeader("If-Modified-Since", ifModifiedSince)
                    .as(Manifest.class)
                    .get();
        }

        private File getManifestFile(final ManifestFile file, long lastSyncTime)
                throws ExecutionException, InterruptedException {
            final String ifModifiedSince = lastSyncTime < 0 ? null :
                    IFMODIFIEDSINCE_FORMAT.format(new Date(TimeUnit.SECONDS.toMillis(lastSyncTime)));

            final Response<File> response = Ion.with(getContext())
                    .load(BuildConfig.SYNC_BASE_URL + file.getName())
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            Timber.d("Downloading [%s] (%s)",
                                    file.getType(), downloaded / (1f * total));
                            // TODO: Broadcast progress..
                        }
                    })
                    .addHeader("If-Modified-Since", ifModifiedSince)
                    .write(new File(getContext().getFilesDir(), file.getName()))
                    .withResponse()
                    .setCallback(new FutureCallback<Response<File>>() {
                        @Override
                        public void onCompleted(Exception e, Response<File> result) {
                            if (e != null ||
                                    result.getHeaders().getResponseCode() >= HttpStatus.SC_BAD_REQUEST) {
                                final File file = result.getResult();
                                if (file != null && file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    })
                    .get();

            if (response.getException() != null) {
                throw new RuntimeException(response.getException());
            } else if (response.getHeaders().getResponseCode() >= HttpStatus.SC_BAD_REQUEST) {
                throw new RuntimeException("Error getting manifest file: " + response.getHeaders());
            } else {
                return response.getResult();
            }
        }

        private boolean hasDatabaseFiles() {
            final File filesDir = getContext().getFilesDir();
            if (filesDir != null && filesDir.exists() && filesDir.isDirectory()) {
                File[] files = filesDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().endsWith(ManifestFile.DATABASE_FILE_SUFFIX);
                    }
                });

                return files.length == ManifestFile.Type.values().length;
            }

            return false;
        }

        private static long getCurrentTimeInSeconds() {
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
    }

}
