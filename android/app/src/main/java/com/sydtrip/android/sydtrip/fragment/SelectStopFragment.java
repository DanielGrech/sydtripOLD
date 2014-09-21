package com.sydtrip.android.sydtrip.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sydtrip.android.sydtrip.R;
import com.sydtrip.android.sydtrip.adapter.BaseViewConvertingAdapter;
import com.sydtrip.android.sydtrip.data.loader.StopLoader;
import com.sydtrip.android.sydtrip.model.Stop;
import com.sydtrip.android.sydtrip.model.sync.ManifestFile;
import com.sydtrip.android.sydtrip.util.Api;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectStopFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Stop>> {

    private static final int LOADER_ID_STOPS = 0x01;

    @InjectView(R.id.list)
    ListView mList;

    @InjectView(R.id.fab)
    View mFloatingActionButton;

    private StopAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_select_stop, container, false);
        ButterKnife.inject(this, view);

        setupFab();

        mList.setAdapter(mAdapter = new StopAdapter());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(LOADER_ID_STOPS, this);
    }

    private void setupFab() {
        if (Api.isMin(Api.L)) {
            int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
            Outline outline = new Outline();
            outline.setOval(0, 0, size, size);
            mFloatingActionButton.setOutline(outline);


            final ImageButton btn = (ImageButton) mFloatingActionButton;
            btn.setImageResource(R.drawable.ic_action_search);
        }
    }

    @Override
    public Loader<List<Stop>> onCreateLoader(int id, Bundle bundle) {
        // TODO: Customize!
        return new StopLoader(getActivity(), ManifestFile.Type.RAIL);
    }

    @Override
    public void onLoadFinished(Loader<List<Stop>> listLoader, List<Stop> stops) {
        mAdapter.populate(stops);
    }

    @Override
    public void onLoaderReset(Loader<List<Stop>> listLoader) {
        mAdapter.populate(null);
    }

    private static class StopAdapter extends BaseViewConvertingAdapter<Stop, TextView> {

        @Override
        protected TextView createView(ViewGroup parent) {
            final TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            return tv;
        }

        @Override
        protected void populateView(Stop stop, TextView view) {
            view.setText(stop.getName());
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }
    }
}
