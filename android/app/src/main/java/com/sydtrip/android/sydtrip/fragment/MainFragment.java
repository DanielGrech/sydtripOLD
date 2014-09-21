package com.sydtrip.android.sydtrip.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.query.QueryResult;

import butterknife.ButterKnife;

public class MainFragment extends BaseQueryFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_main, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onQueryResult(QueryResult result) {

    }
}
