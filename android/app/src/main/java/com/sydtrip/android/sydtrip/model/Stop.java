package com.sydtrip.android.sydtrip.model;

import android.os.Parcel;

public class Stop extends BaseModel {

    private int mId;

    private String mCode;

    private String mName;

    private float mLat;

    private float mLng;

    private int mType;

    private String mPlatformCode;

    private int mParentId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getLat() {
        return mLat;
    }

    public void setLat(float lat) {
        mLat = lat;
    }

    public float getLng() {
        return mLng;
    }

    public void setLng(float lng) {
        mLng = lng;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getPlatformCode() {
        return mPlatformCode;
    }

    public void setPlatformCode(String platformCode) {
        mPlatformCode = platformCode;
    }

    public int getParentId() {
        return mParentId;
    }

    public void setParentId(int parentId) {
        this.mParentId = parentId;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mCode);
        parcel.writeString(mName);
        parcel.writeFloat(mLat);
        parcel.writeFloat(mLng);
        parcel.writeInt(mType);
        parcel.writeString(mPlatformCode);
        parcel.writeInt(mParentId);
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel parcel) {
            final Stop stop = new Stop();
            stop.mId = parcel.readInt();
            stop.mCode = parcel.readString();
            stop.mName = parcel.readString();
            stop.mLat = parcel.readFloat();
            stop.mLng = parcel.readFloat();
            stop.mType = parcel.readInt();
            stop.mPlatformCode = parcel.readString();
            stop.mParentId = parcel.readInt();

            return stop;
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
}
