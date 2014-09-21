package com.sydtrip.android.sydtrip.fragment;

import android.graphics.Outline;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.query.QueryResult;
import com.sydtrip.android.sydtrip.util.Api;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainFragment extends BaseQueryFragment {

    @InjectView(R.id.fab)
    View mFloatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_main, container, false);
        ButterKnife.inject(this, view);

        if (Api.isMin(Api.L)) {
            int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
            Outline outline = new Outline();
            outline.setOval(0, 0, size, size);
            mFloatingActionButton.setOutline(outline);
        }

        return view;
    }

    @Override
    public void onQueryResult(QueryResult result) {

    }
}
