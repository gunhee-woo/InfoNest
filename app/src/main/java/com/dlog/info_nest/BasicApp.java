package com.dlog.info_nest;

import android.app.Application;

import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.utilities.AppExecutors;

public class BasicApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getDatabase());
    }

}
