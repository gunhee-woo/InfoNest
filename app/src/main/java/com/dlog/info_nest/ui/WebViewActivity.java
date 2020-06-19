package com.dlog.info_nest.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.WebviewActivityBinding;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.utilities.MyWebViewClient;
import com.dlog.info_nest.utilities.UrlCrawling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.dlog.info_nest.utilities.CurrentDateKt.currentDate;

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
        String intentUrl = getIntent().getStringExtra("url");
        if(intentUrl != null) {
            mWebviewActivityBinding.webView.setWebViewClient(new MyWebViewClient(getApplicationContext()));
            mWebSetting = mWebviewActivityBinding.webView.getSettings();
            mWebSetting.setJavaScriptEnabled(true);
            mWebviewActivityBinding.webView.loadUrl(intentUrl);
        } else {
            mWebviewActivityBinding.webView.setWebViewClient(new MyWebViewClient(getApplicationContext()));
            mWebSetting = mWebviewActivityBinding.webView.getSettings();
            mWebSetting.setJavaScriptEnabled(true);
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
                Toast.makeText(this, "더이상 뒤로갈 수 없습니다", Toast.LENGTH_SHORT).show();
        });

        mWebviewActivityBinding.btnFront.setOnClickListener(v -> {
            if(mWebviewActivityBinding.webView.canGoForward())
                mWebviewActivityBinding.webView.goForward();
            else
                Toast.makeText(this, "더이상 앞으로갈 수 없습니다", Toast.LENGTH_SHORT).show();
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
            finish();
        });
    }


    public Bitmap byteArrayToBitmap(byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }

    @Override
    public void onBackPressed() {
        if(mWebviewActivityBinding.webView.canGoBack())
            mWebviewActivityBinding.webView.goBack();
        else {
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
            this.tag = "";
            this.color = -1;
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
                String imageUrl;
                byte[] image = null;
                try {
                    imageUrl = urlCrawling.getUrlToImageUrl();
                    image = getByteArrayImage(imageUrl);
                } catch (Exception e) {
                    e.toString();
                }
                BookmarkEntity bookmarkEntity = new BookmarkEntity(title, url, tag, currentDate(), color,
                        urlCrawling.getUrlToTop10NounsArray(), image, false, false);
                mDataRepository.insert(bookmarkEntity);
                return true;
            } catch (Exception e) {
                e.toString();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b) {
                Toast.makeText(context, "북마크가 저장되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "북마크 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
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