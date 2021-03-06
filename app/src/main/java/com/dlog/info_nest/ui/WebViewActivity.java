package com.dlog.info_nest.ui;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.MainActivity;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.WebviewActivityBinding;
import com.dlog.info_nest.db.WidgetDB2;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.db.entity.WidgetItem2;
import com.dlog.info_nest.ui.palette.My_Widget_Provider2;
import com.dlog.info_nest.utilities.MyWebViewClient;
import com.dlog.info_nest.utilities.UrlCrawling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.dlog.info_nest.utilities.CurrentDateKt.currentDate;
import static com.dlog.info_nest.utilities.UrlCrawling.addDelay;

public class WebViewActivity extends AppCompatActivity {
    private WebviewActivityBinding mWebviewActivityBinding;
    private WebSettings mWebSetting;
    private static DataRepository mDataRepository;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataRepository = ((BasicApp) getApplication()).getDataRepository();
        mWebviewActivityBinding = DataBindingUtil.setContentView(this, R.layout.webview_activity);

        mWebviewActivityBinding.webView.setWebViewClient(new MyWebViewClient(getApplicationContext()));
        mWebSetting = mWebviewActivityBinding.webView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);

        Uri intentUri = getIntent().getData();
        String url = getIntent().getStringExtra("url");
        if(intentUri != null) {

            mWebviewActivityBinding.webView.loadUrl(intentUri.toString());
        } else if (url != null){

            mWebviewActivityBinding.webView.loadUrl(url);
        } else {

            mWebviewActivityBinding.webView.loadUrl("https://www.google.com");
        }
        setBtnListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnListener() {
        mWebviewActivityBinding.btnBack.setOnClickListener(v -> {
            if(mWebviewActivityBinding.webView.canGoBack())
                mWebviewActivityBinding.webView.goBack();
            else
                Toast.makeText(this, "????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
        });

        mWebviewActivityBinding.btnFront.setOnClickListener(v -> {
            if(mWebviewActivityBinding.webView.canGoForward())
                mWebviewActivityBinding.webView.goForward();
            else
                Toast.makeText(this, "????????? ???????????? ??? ????????????", Toast.LENGTH_SHORT).show();
        });

        mWebviewActivityBinding.btnBookmark.setOnClickListener(v -> {
            String url = mWebviewActivityBinding.webView.getUrl();
            String title = mWebviewActivityBinding.webView.getTitle();
            Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("activity", "webView");
            startActivity(intent);
        });

        mWebviewActivityBinding.btnRefresh.setOnClickListener(v -> {
            mWebviewActivityBinding.webView.reload();
        });

        mWebviewActivityBinding.btnClose.setOnClickListener(v -> {
            if(!MainActivity.isMainRunning ){
                //Main??? ??????
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
            finish();
        });
    }

    @Override
    public void onBackPressed() {

        if(mWebviewActivityBinding.webView.canGoBack())
            mWebviewActivityBinding.webView.goBack();
        else {

            if(!MainActivity.isMainRunning ){
                //Main??? ??????
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
            finish();
        }
    }


    public static class networkAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String title;
        private String url;
        private String tag;
        private int color;

        public networkAsyncTask(Context context, String title, String url) {
            this.context = context;
            this.title = title;
            this.url = url;
            this.tag = title; // ????????????
            this.color = 0;
        }

        public networkAsyncTask(Context context, String title, String url, String tag, int color) {
            this.context = context;
            this.title = title;
            this.url = url;
            this.tag = tag;
            this.color = color;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                UrlCrawling urlCrawling = new UrlCrawling(url);
                addDelay();
                String imageUrl;
                byte[] image = null;
                try {
                    imageUrl = urlCrawling.getUrlToImageUrl();
                    image = getByteArrayImage(imageUrl);
                } catch (Exception e) {
                    e.toString();
                }
                BookmarkEntity bookmarkEntity = new BookmarkEntity(title, url, tag, currentDate(), color,
                        Arrays.asList(tag.split(" ")), image, false, false);
                if(mDataRepository == null){
                    mDataRepository = ((BasicApp) context).getDataRepository();
                }
                mDataRepository.insert(bookmarkEntity);

                //???????????? ??????
                WidgetDB2 db = Room.databaseBuilder(context, WidgetDB2.class, "widget_list").build();
                db.widgetDao2().insertWidgetItem(new WidgetItem2(title, url, 0, 0));
                //list ?????? ????????????
                //???????????? ????????? ?????????
                Intent intent = new Intent(context, My_Widget_Provider2.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                context.sendBroadcast(intent);
                return true;
            } catch (Exception e) {
                Log.d("TTT", "????????? ???????????? : " + e.getMessage());
                e.toString();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b) {
                Toast.makeText(context, "???????????? ?????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "????????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static byte[] getByteArrayImage(String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        URL u = null;
        try {
            u = new URL(url);
            is = u.openStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", u.toExternalForm(), e.getMessage());
            e.printStackTrace();
            // Perform any other exception handling that's appropriate.
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos.toByteArray();
    }
}
