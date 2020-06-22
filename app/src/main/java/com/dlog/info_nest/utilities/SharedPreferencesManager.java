package com.dlog.info_nest.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    /**
     * 위젯 배경 color preferences
     */
    private SharedPreferences widgetBackgroundPrefs;
    /**
     * clipboard data preferences
     */
    private SharedPreferences clipboardDataPrefs;

    public SharedPreferencesManager(Context context){
        widgetBackgroundPrefs = context.getSharedPreferences("widget_back_color",Context.MODE_PRIVATE);
        clipboardDataPrefs = context.getSharedPreferences("clipboard_data", Context.MODE_PRIVATE);
    }

    public String getWidgetBackgroundColorPrefs(){
        return widgetBackgroundPrefs.getString("widget_back_color","");
    }

    public String getClipboardDataPrefs() {
        return clipboardDataPrefs.getString("clipboard_data", "");
    }

    public void setClipboardDataPrefs(String val) {
        SharedPreferences.Editor editor = clipboardDataPrefs.edit();
        editor.putString("clipboard_data",val);
        editor.commit();
    }

    /**
     * 파라미터로 받은 value 값을 {@link SharedPreferencesManager#widgetBackgroundPrefs}에 저장한다.
     * @param value
     */
    public void saveWidgetBackgroundPrefs(String value){
        SharedPreferences.Editor editor = widgetBackgroundPrefs.edit();
        editor.putString("widget_back_color",value);
        editor.commit();
    }

}
