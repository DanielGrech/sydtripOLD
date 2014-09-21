package com.sydtrip.android.sydtrip.model;

import android.os.Parcel;

public class StopTime extends BaseModel {

    private int mTripId;

    private int mStopId;

    private int mSecondsSinceMidnight;

    public int getTripId() {
        return mTripId;
    }

    public void setTripId(int tripId) {
        mTripId = tripId;
    }

    public int getStopId() {
        return mStopId;
    }

    public void setStopId(int stopId) {
        mStopId = stopId;
    }

    public int getSecondsSinceMidnight() {
        return mSecondsSinceMidnight;
    }

    public void setSecondsSinceMidnight(int secondsSinceMidnight) {
        mSecondsSinceMidnight = secondsSinceMidnight;
    }

    @Override
    public String toString() {
        return "StopTime{" +
                "mTripId=" + mTripId +
                ", mStopId=" + mStopId +
                ", mSecondsSinceMidnight=" + mSecondsSinceMidnight +
                '}';
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mTripId);
        parcel.writeInt(mStopId);
        parcel.writeInt(mSecondsSinceMidnight);
    }

    public static final Creator<StopTime> CREATOR = new Creator<StopTime>() {
        @Override
        public StopTime createFromParcel(Parcel parcel) {
            final StopTime stopTime = new StopTime();
            stopTime.mTripId = parcel.readInt();
            stopTime.mStopId = parcel.readInt();
            stopTime.mSecondsSinceMidnight = parcel.readInt();

            return stopTime;
        }

        @Override
        public StopTime[] newArray(int size) {
            return new StopTime[size];
        }
    };
}
