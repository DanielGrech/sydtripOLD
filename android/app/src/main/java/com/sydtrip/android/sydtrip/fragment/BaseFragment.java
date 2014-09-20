package com.sydtrip.android.sydtrip.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import com.sydtrip.android.sydtrip.IAnalytics;
import com.sydtrip.android.sydtrip.STApp;
import com.sydtrip.android.sydtrip.activity.BaseActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Base class for all fragments in the app
 */
public abstract class BaseFragment extends Fragment {

    @Inject
    protected Bus mEventBus;

    @Inject
    protected IAnalytics mAnalytics;

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof BaseActivity) {
            super.onAttach(activity);
        } else {
            throw new IllegalStateException(
                    "Expected to be attached to a BaseActivity");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final STApp app = (STApp) getActivity().getApplication();
        app.inject(this);
    }

    /**
     * Reload the data from a loader
     *
     * @param loaderId  The id of the loader to reload
     * @param callbacks The callback to be invoked by the underlying loader
     */
    protected void reload(int loaderId, LoaderManager.LoaderCallbacks callbacks) {
        final Loader loader = getLoaderManager().getLoader(loaderId);
        if (loader == null) {
            getLoaderManager().initLoader(loaderId, null, callbacks);
        } else {
            getLoaderManager().restartLoader(loaderId, null, callbacks);
        }
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
