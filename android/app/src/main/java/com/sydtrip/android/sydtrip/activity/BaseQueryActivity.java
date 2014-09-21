package com.sydtrip.android.sydtrip.activity;

import android.os.Bundle;

import com.sydtrip.android.sydtrip.model.Query;
import com.sydtrip.android.sydtrip.service.QueryServiceConnection;

public abstract class BaseQueryActivity extends BaseActivity
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
        mServiceConnection.bind(this);
    }

    @Override
    public void onStop() {
        mServiceConnection.unbind(this);
        super.onStop();
    }

    protected void query(Query query) {
        if (mServiceConnection.isConnected()) {
            mServiceConnection.query(query);
        }
    }
}
