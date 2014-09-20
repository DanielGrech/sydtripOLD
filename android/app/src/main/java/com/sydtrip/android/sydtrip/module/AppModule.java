package com.sydtrip.android.sydtrip.module;

import android.content.Context;
import com.sydtrip.android.sydtrip.STApp;
import com.sydtrip.android.sydtrip.module.annotation.ForApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Provides injection of our global {@link android.app.Application} object
 */
@Module(
        library = true
)
public class AppModule {

    private STApp mApp;

    public AppModule(STApp app) {
        mApp = app;
    }

    @Provides
    @ForApplication
    public Context providesApplicationContext() {
        return mApp;
    }
}
