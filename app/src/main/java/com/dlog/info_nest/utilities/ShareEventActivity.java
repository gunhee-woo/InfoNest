package com.dlog.info_nest.utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dlog.info_nest.ui.PopupActivity;

// 다른브라우저에서 공유하기 클릭 시 팝업 액티비티로 넘어가는 기능
public class ShareEventActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            String url = intent.getStringExtra("originalUrl");
            String title = intent.getStringExtra(Intent.EXTRA_TEXT).split("\r\n")[0];
            Intent intent1 = new Intent(getApplicationContext(), PopupActivity.class);
            intent1.putExtra("url", url);
            intent1.putExtra("title", title);
            intent1.putExtra("activity", "webView");
            startActivity(intent1);
        }
        finish();
    }
}
