package com.sydtrip.android.sydtrip.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.query.Query;
import com.sydtrip.android.sydtrip.query.QueryResult;

import butterknife.ButterKnife;

public class MainActivity extends BaseQueryActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    public void onQueryResult(QueryResult result) {
//        Toast.makeText(this, "Got result: " + result, Toast.LENGTH_SHORT).show();
    }
}
