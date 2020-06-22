package com.dlog.info_nest.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceActivity {
    private SharedPreferences preferences;

    public SharedPreferenceActivity(Context context) {
        preferences = context.getSharedPreferences("password", Context.MODE_PRIVATE);
    }

    public String getPasswordPreferences() {
        return preferences.getString("password", "");
    }

    public void savePreferences(String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password",value);
        editor.commit();
    }
}
