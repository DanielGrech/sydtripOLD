package com.sydtrip.android.sydtrip.adapter;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Base class for showing a list of items in an {@link android.widget.AdapterView}
 *
 * @param <DataType> The model type to display
 */
public abstract class BaseListAdapter<DataType> extends BaseAdapter {

    private List<DataType> items;

    @Override
    public final int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public final DataType getItem(int position) {
        return items.get(position);
    }

    /**
     * Reload the adapter with the given items
     *
     * @param items The items to show in the adapter
     */
    public final void populate(List<DataType> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}