package com.sydtrip.android.sydtrip.model;

import android.os.Parcel;

public class Route extends BaseModel {

    private int mId;

    private String mShortName;

    private String mLongName;

    private int mColor;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        this.mShortName = shortName;
    }

    public String getLongName() {
        return mLongName;
    }

    public void setLongName(String longName) {
        this.mLongName = longName;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeString(mShortName);
        parcel.writeString(mLongName);
        parcel.writeInt(mColor);
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel parcel) {
            final Route route = new Route();
            route.mId = parcel.readInt();
            route.mShortName = parcel.readString();
            route.mLongName = parcel.readString();
            route.mColor = parcel.readInt();

            return route;
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
