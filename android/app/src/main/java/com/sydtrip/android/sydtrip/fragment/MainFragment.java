package com.sydtrip.android.sydtrip.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.activity.SelectStopActivity;
import com.sydtrip.android.sydtrip.query.QueryResult;
import com.sydtrip.android.sydtrip.util.Api;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

            final ImageButton btn = (ImageButton) mFloatingActionButton;
            btn.setImageResource(R.drawable.ic_action_add);
        }

        return view;
    }

    @Override
    public void onQueryResult(QueryResult result) {

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void onFabClicked(final View view) {
        final int w = view.getWidth();
        final int h = view.getWidth();
        final ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(view, w / 2, h / 2, w, h);

        final Intent intent = new Intent(getActivity(), SelectStopActivity.class);
        startActivity(intent, opts.toBundle());
    }
}
