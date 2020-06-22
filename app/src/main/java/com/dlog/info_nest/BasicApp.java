package com.dlog.info_nest;

import android.app.Application;

import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.utilities.AppExecutors;
import com.dlog.info_nest.utilities.SharedPreferenceActivity;

public class BasicApp extends Application {
    public static SharedPreferenceActivity prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new SharedPreferenceActivity(getApplicationContext());
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getDatabase());
    }

}
