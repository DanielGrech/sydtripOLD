package com.sydtrip.android.sydtrip.activity;

import android.os.Bundle;

import com.sydtrip.android.sydtrip.R;

public class SelectStopActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_stop);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
