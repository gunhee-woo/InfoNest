package com.dlog.info_nest;

import android.app.Application;

import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.utilities.SharedPreferenceActivity;
import com.dlog.info_nest.utilities.SharedPreferencesManager;

public class BasicApp extends Application {
    public static SharedPreferenceActivity prefs;
    public static SharedPreferencesManager prefsManager;
    public static String baseUrl = "http://221.146.248.179:7229";

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new SharedPreferenceActivity(getApplicationContext());
        prefsManager = new SharedPreferencesManager(getApplicationContext());
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getDatabase());
    }

}
