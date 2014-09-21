package com.sydtrip.android.sydtrip.module;

import com.sydtrip.android.sydtrip.dao.IDao;
import com.sydtrip.android.sydtrip.dao.StopTimeDao;
import com.sydtrip.android.sydtrip.model.StopTime;
import com.sydtrip.android.sydtrip.util.DaoUtils;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Provides access to our model DAO objects
 */
@Module(
        staticInjections = {
                DaoUtils.class
        }
)
public class DaoModule {

    @Provides
    @Singleton
    public IDao<StopTime> providesStopTimeDao() {
        return new StopTimeDao();
    }
}
