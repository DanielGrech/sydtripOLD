package com.sydtrip.android.sydtrip.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Adapter which shows a list of items and adds view recycling
 *
 * @param <DataType> The model type to display in the adapter
 * @param <ViewType> The type of view to display in each item
 */
public abstract class BaseViewConvertingAdapter<DataType, ViewType extends View>
        extends BaseListAdapter<DataType> {

    /**
     * Instantiate a new view for display
     *
     * @param parent The parent {@link android.view.ViewGroup} where the view will be placed
     * @return A view to show in the adapter
     */
    protected abstract ViewType createView(ViewGroup parent);

    /**
     * Populates the view with a specific {@link DataType}
     *
     * @param data The item to display
     * @param view The view to display the item in
     */
    protected abstract void populateView(DataType data, ViewType view);

    @SuppressWarnings("unchecked")
    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        final ViewType view;
        if (convertView == null) {
            view = createView(parent);
        } else {
            view = (ViewType) convertView;
        }

        populateView(getItem(position), view);

        return view;
    }

}