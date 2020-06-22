package com.dlog.info_nest.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 재부팅 되었을 때 서비스 재등록.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG","BootReceiver onReceive ");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("TAG", context.getPackageName());
            Log.d("TAG","boot completed received");
            Intent serviceIntent = new Intent(context, ClipboardService.class);
            context.startService(serviceIntent);
        }
    }

}
