package com.sydtrip.android.sydtrip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.SyncRequest;
import android.os.Bundle;

import com.google.android.gms.drive.DriveApi;
import com.koushikdutta.ion.Ion;
import com.sydtrip.android.sydtrip.module.AppModule;
import com.sydtrip.android.sydtrip.module.AppServicesModule;
import com.sydtrip.android.sydtrip.module.ApiModule;
import com.sydtrip.android.sydtrip.module.DaoModule;
import com.sydtrip.android.sydtrip.sync.AuthenticatorService;
import com.sydtrip.android.sydtrip.util.Api;
import com.sydtrip.android.sydtrip.util.ReleaseLogger;

import java.util.Random;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 *
 */
public class STApp extends Application implements Application.ActivityLifecycleCallbacks {

    private ObjectGraph mObjectGraph;

    /**
     * The number of activities the app is showing
     */
    private int mActivityCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            //Default logger
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogger(getClass().getSimpleName()));
        }

        mObjectGraph = ObjectGraph.create(getDiModules());
        mObjectGraph.injectStatics();

        registerActivityLifecycleCallbacks(this);

        Ion.getDefault(this).getConscryptMiddleware().enable(false);

        setupSync();
    }

    /**
     * The base set of DI modules to inject app components with
     */
    private Object[] getDiModules() {
        return new Object[]{
                new AppModule(this),
                new AppServicesModule(),
                new ApiModule(),
                new DaoModule(),
        };
    }

    /**
     * Inject the given object
     *
     * @param obj          The obejct to inject
     * @param extraModules Any additional modules to include in the injection
     */
    public void inject(Object obj, Object... extraModules) {
        ObjectGraph og = mObjectGraph;
        if (extraModules != null && extraModules.length > 0) {
            og = mObjectGraph.plus(extraModules);
        }
        og.inject(obj);
    }

    public <T> T getInjection(Class<T> cls) {
        return mObjectGraph.get(cls);
    }

    /**
     * @return <code>true</code> if the app has at least 1 activity visible,
     * <code>false otherwise</code>
     */
    public boolean isAppInForeground() {
        return mActivityCounter > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCounter++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCounter = Math.max(0, mActivityCounter - 1);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void requestSync() {
        final Account account = AuthenticatorService.ACCOUNT;
        final String auth = BuildConfig.SYNC_CONTENT_PROVIDER_AUTHORITY;

        if (Api.isMin(Api.KITKAT)) {
            ContentResolver.requestSync(new SyncRequest.Builder()
                    .setSyncAdapter(account, auth)
                    .setExtras(new Bundle())
                    .setManual(true)
                    .setExpedited(true)
                    .syncOnce()
                    .build());
        } else {
            final Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(account, auth, settingsBundle);
        }
    }

    private void setupSync() {
        final Account account = AuthenticatorService.ACCOUNT;
        final String auth = BuildConfig.SYNC_CONTENT_PROVIDER_AUTHORITY;
        if (AccountManager.get(this).addAccountExplicitly(account, null, null)) {
            ContentResolver.setSyncAutomatically(account, auth, true);

            if (Api.isMin(Api.KITKAT)) {
                ContentResolver.requestSync(new SyncRequest.Builder()
                        .setSyncAdapter(account, auth)
                        .syncPeriodic(BuildConfig.SYNC_POLL_FREQUENCY, BuildConfig.SYNC_POLL_FLEX)
                        .setExtras(new Bundle())
                        .build());
            } else {
                final int max = (int) (BuildConfig.SYNC_POLL_FREQUENCY + BuildConfig.SYNC_POLL_FLEX);
                final int min = (int) (BuildConfig.SYNC_POLL_FREQUENCY - BuildConfig.SYNC_POLL_FLEX);

                final long syncPeriod = new Random().nextInt((max - min) + 1) + min;
                ContentResolver.addPeriodicSync(account, auth, new Bundle(), syncPeriod);
            }
        }

        requestSync();
    }
}
