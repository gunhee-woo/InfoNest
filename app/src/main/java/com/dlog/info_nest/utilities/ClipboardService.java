package com.dlog.info_nest.utilities;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dlog.info_nest.BasicApp;

public class ClipboardService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    ClipboardManager mManager;
    Context context = this;
    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 리스너 등록
        mManager.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 리스너 해제
        mManager.removePrimaryClipChangedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrimaryClipChanged() {
        if (mManager != null && mManager.getPrimaryClip() != null) {
            final ClipData data = mManager.getPrimaryClip();

            // 한번의 복사로 복수 데이터를 넣었을 수 있으므로, 모든 데이터를 가져온다.
            int dataCount = data.getItemCount();
            for (int i = 0 ; i < dataCount ; i++) {
                final int finalI = i;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BasicApp.prefsManager.setClipboardDataPrefs(data.getItemAt(finalI).coerceToText(context).toString());
                    }
                });
                t.start();
                Log.e("Test", "clip data - item : "+data.getItemAt(i).coerceToText(this));
            }
        } else {
            Log.e("Test", "No Manager or No Clip data");
        }
    }
}
