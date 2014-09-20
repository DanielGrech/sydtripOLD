package com.sydtrip.android.sydtrip.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.sydtrip.android.sydtrip.IAnalytics;
import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.STApp;
import com.sydtrip.android.sydtrip.jobs.BaseJob;
import com.sydtrip.android.sydtrip.receiver.ApiBroadcastReceiver;
import com.sydtrip.android.sydtrip.util.UiUtils;
import com.sydtrip.android.sydtrip.util.Api;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Base class for all activities in the app
 */
public abstract class BaseActivity extends Activity {

    @Inject
    protected Bus mEventBus;

    @Inject
    protected IAnalytics mAnalytics;

    protected STApp mApp;

    /**
     * Catches API-related broadcasts
     */
    private ApiBroadcastReceiver mApiReceiver = new ApiBroadcastReceiver() {

        @Override
        protected <T extends BaseJob> void onStart(Class<T> jobCls, String token) {
//            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestStart(jobCls, token);
        }

        @Override
        protected <T extends BaseJob> void onFinish(Class<T> jobCls, String token, Bundle results) {
//            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestFinish(jobCls, token, results);
        }

        @Override
        protected <T extends BaseJob> void onError(Class<T> jobCls, String token, String errorMsg) {
            onJobRequestError(jobCls, token, errorMsg);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mApp = (STApp) getApplication();
        mApp.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApiReceiver.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected <T extends BaseJob> void onJobRequestStart(Class<T> cls, String action) {
        //No-op
    }

    protected <T extends BaseJob> void onJobRequestFinish(Class<T> cls, String action,
                                                          Bundle results) {
        //No-op
    }

    protected <T extends BaseJob> void onJobRequestError(
            Class<T> cls, String action, String errorMsg) {
        //No-op
    }

    /**
     * Listen out for API broadcasts of type <code>token</code>
     *
     * @param token The token returned from a method in {@link com.sydtrip.android.sydtrip.jobs.BaseJob}
     */
    protected void registerForJob(String token) {
        mApiReceiver.addAcceptableToken(token);
    }

    protected <T extends BaseJob> void registerForJobClass(Class<T> cls) {
        mApiReceiver.addAcceptableJobType(cls);
    }

    /**
     * @param id  The id of the fragment to retrieve
     * @param <T> A {@link Fragment} subclass
     * @return The fragment with id <code>id</code>, or null if it doesn't exist
     */
    protected <T extends Fragment> T findFragment(int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }
}