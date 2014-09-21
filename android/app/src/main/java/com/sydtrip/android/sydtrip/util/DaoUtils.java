package com.sydtrip.android.sydtrip.util;

import android.content.ContentValues;
import android.database.Cursor;
import com.sydtrip.android.sydtrip.dao.IDao;
import com.sydtrip.android.sydtrip.model.StopTime;

import javax.inject.Inject;

/**
 * Provides a single point of conversion for all dao-related objects
 */
public class DaoUtils {

    @Inject
    static IDao<StopTime> sStopTimeDao;

    public static ContentValues convert(Object obj) {
        IDao dao = getDao(obj.getClass());
        return dao.convert(obj);
    }

    public static <T> T build(Class<T> cls, Cursor cursor) {
        IDao<T> dao = getDao(cls);
        return dao.build(cursor);
    }

    public static <T> IDao<T> getDao(Class<T> cls) {
        if (cls.equals(StopTime.class)) {
            return (IDao<T>) sStopTimeDao;
        }

        throw new IllegalArgumentException("Unknown dao class: " + cls);
    }

}
