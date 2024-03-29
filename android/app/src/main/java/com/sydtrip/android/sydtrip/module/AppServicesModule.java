package com.sydtrip.android.sydtrip.module;

import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.sydtrip.android.sydtrip.Analytics;
import com.sydtrip.android.sydtrip.IAnalytics;
import com.sydtrip.android.sydtrip.BuildConfig;
import com.sydtrip.android.sydtrip.STApp;
import com.sydtrip.android.sydtrip.activity.SelectStopActivity;
import com.sydtrip.android.sydtrip.fragment.MainFragment;
import com.sydtrip.android.sydtrip.fragment.SelectStopFragment;
import com.sydtrip.android.sydtrip.module.annotation.ForApplication;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.google.android.gms.analytics.GoogleAnalytics;

import com.sydtrip.android.sydtrip.activity.MainActivity;
import com.sydtrip.android.sydtrip.sync.SyncService;
import com.sydtrip.android.sydtrip.util.PrefUtils;

import javax.inject.Singleton;

/**
 * Provides access to the different services required in the app
 */
@Module(
        complete = false,
        library = true,
        injects = {
                MainActivity.class,
                SelectStopActivity.class,

                MainFragment.class,
                SelectStopFragment.class,

                SyncService.class
        }
)
public class AppServicesModule {

    @Provides
    @Singleton
    public PrefUtils providesPrefUtils(@ForApplication Context context) {
        return new PrefUtils(context);
    }

    @Provides
    @Singleton
    public IAnalytics providesAnalytics(@ForApplication Context context) {
        return new Analytics(GoogleAnalytics.getInstance(context));
    }

    @Provides
    @Singleton
    public Bus providesBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides
    @Singleton
    public LocalBroadcastManager providesLocalBroadcastManager(@ForApplication Context context) {
        return LocalBroadcastManager.getInstance(context.getApplicationContext());
    }

    @Provides
    @Singleton
    public ContentResolver providesContentResolver(@ForApplication Context context) {
        return context.getApplicationContext().getContentResolver();
    }

    @Provides
    @Singleton
    public JobManager providesJobManager(@ForApplication final Context context) {
        final STApp app = (STApp) context.getApplicationContext();
        final DependencyInjector injector = new DependencyInjector() {
            @Override
            public void inject(final BaseJob job) {
                app.inject(job);
            }
        };

        final CustomLogger logger = new CustomLogger() {
            @Override
            public boolean isDebugEnabled() {
                return BuildConfig.DEBUG;
            }

            @Override
            public void d(final String message, final Object... args) {
                Timber.d(message, args);
            }

            @Override
            public void e(final Throwable throwable, final String message, final Object... args) {
                Timber.e(throwable, message, args);
            }

            @Override
            public void e(final String message, final Object... args) {
                Timber.e(message, args);
            }
        };

        final Configuration config = new Configuration.Builder(app)
                .customLogger(logger)
                .injector(injector)
                .build();

        return new JobManager(app, config);
    }
}
