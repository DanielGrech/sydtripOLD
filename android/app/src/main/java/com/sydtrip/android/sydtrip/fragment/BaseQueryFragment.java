package com.sydtrip.android.sydtrip.fragment;

import android.os.Bundle;

import com.sydtrip.android.sydtrip.query.Query;
import com.sydtrip.android.sydtrip.service.QueryServiceConnection;

public abstract class BaseQueryFragment extends BaseFragment
        implements QueryServiceConnection.QueryCallback {

    private QueryServiceConnection mServiceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceConnection = new QueryServiceConnection(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mServiceConnection.bind(getActivity());
    }

    @Override
    public void onStop() {
        mServiceConnection.unbind(getActivity());
        super.onStop();
    }

    protected void query(Query query) {
        mServiceConnection.query(query);
    }
}
