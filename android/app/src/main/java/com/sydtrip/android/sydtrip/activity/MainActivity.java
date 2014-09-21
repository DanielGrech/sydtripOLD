package com.sydtrip.android.sydtrip.activity;

import android.os.Bundle;

import com.sydtrip.android.sydtrip.R;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    protected void onCreate(final Bundle savedInstanceState) {
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
}
