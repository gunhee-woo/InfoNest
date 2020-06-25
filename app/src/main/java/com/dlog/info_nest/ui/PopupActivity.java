package com.dlog.info_nest.ui;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.PopupActivityBinding;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.utilities.Domparser;
import com.dlog.info_nest.utilities.UrlCrawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

import static com.dlog.info_nest.utilities.CurrentDateKt.currentDate;

public class PopupActivity extends AppCompatActivity {

    public static final int[] COLORS = {
            0xFFFFFFFF,
            0xFFF44336,
            0xFFE91E63,
            0xFF9C27B0,
            0xFF3F51B5,
            0xFF03A9F4,
            0xFF8BC34A,
            0xFF6200EE
    };

    private PopupActivityBinding mPopupActivityBinding;
    boolean isOpenDetail = true;
    private int bookmarkColor = -1;
    private DataRepository mDataRepository;
    private BookmarkEntity bookmarkEntity;
    private boolean isIntentMain = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupActivityBinding = DataBindingUtil.setContentView(this, R.layout.popup_activity);
        mDataRepository = ((BasicApp) getApplication()).getDataRepository();

        if(Objects.equals(getIntent().getStringExtra("activity"), "webView")) {
            String url = getIntent().getStringExtra("url");
            String title = getIntent().getStringExtra("title");
            mPopupActivityBinding.editTxtPopupTitle.setText(title);
            mPopupActivityBinding.editTxtPopupUrl.setText(url);
            final String[] lines = new String[2];
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Domparser dp = new Domparser(url);
                    //meta, title 크롤링
                    lines[0] = dp.getDoc();
                    //keyword 추출.
                    try {
                        lines[0] = URLEncoder.encode(lines[0], "UTF-8");
                        URL keywordUrl = new URL(BasicApp.baseUrl+"/textrank/?message=" + lines[0]);
                        HttpURLConnection conn = null; //접속
                        conn = (HttpURLConnection)keywordUrl.openConnection();
                        if(conn != null) {
                            conn.setConnectTimeout(5000);
                            conn.setUseCaches(false);
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                //데이터 읽기
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                                lines[1] = br.readLine();
                                br.close();
                            }
                            else{
                                Log.d("TTT", conn.getResponseMessage());
                            }
                            conn.disconnect();
                        }
                    } catch (UnsupportedEncodingException | MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            try {
                t.join();
                if(lines[1] != null && lines[1].length() != 0) {
                    mPopupActivityBinding.edtPopupTag.setTags(lines[1].split(" "));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(Objects.equals(getIntent().getSerializableExtra("activity"),"main")) { // 메인 피드 스와이프 수정 버튼 클릭 시
            bookmarkEntity = (BookmarkEntity) getIntent().getSerializableExtra("bookmark");
            mPopupActivityBinding.editTxtPopupTitle.setText(bookmarkEntity.getmTitle());
            mPopupActivityBinding.editTxtPopupUrl.setText(bookmarkEntity.getmUrl());
            bookmarkColor = bookmarkEntity.getmColor();
            mPopupActivityBinding.edtPopupTag.setTags(bookmarkEntity.getmTags().split(" "));
            isIntentMain = true;
        }

        //createColorPickerBar();
        setBtnListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnListener() {
        mPopupActivityBinding.btnPopupOk.setOnClickListener(v -> {
            if(bookmarkColor == -1) {
                bookmarkColor = 0;
            }
            String title = mPopupActivityBinding.editTxtPopupTitle.getText().toString();
            String url = mPopupActivityBinding.editTxtPopupUrl.getText().toString();
            String tag = mPopupActivityBinding.edtPopupTag.getText().toString();
            int color = bookmarkColor;

            if(isIntentMain) {
                bookmarkEntity.setmTitle(title);
                bookmarkEntity.setmUrl(url);
                bookmarkEntity.setmTags(tag);
                bookmarkEntity.setmColor(color);
                mDataRepository.update(bookmarkEntity);
                finish();
                return;
            }

            new WebViewActivity.networkAsyncTask(getApplicationContext(), title, url, tag, COLORS[bookmarkColor]).execute();

            setResult(1);
            finish();
        });

        mPopupActivityBinding.btnPopupNo.setOnClickListener(v -> {
            finish();
        });

        mPopupActivityBinding.btnDetailPopupSetting.setOnClickListener(v -> {
            if(isOpenDetail) {
                isOpenDetail = false;
                mPopupActivityBinding.layoutDetailPopup.setVisibility(View.VISIBLE);
                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp);
                mPopupActivityBinding.btnDetailPopupSetting.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            } else {
                isOpenDetail = true;
                mPopupActivityBinding.layoutDetailPopup.setVisibility(View.GONE);
                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp);
                mPopupActivityBinding.btnDetailPopupSetting.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);

            }

        });

        mPopupActivityBinding.btnPopupFixedpin.setOnClickListener(v -> {

        });
    }
    /*
    public void createColorPickerBar() {
        int ix = 0;
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMarginStart(15);
        mLayoutParams.setMarginEnd(15);
        mLayoutParams.setMargins(0,5,0,5);
        for(int color : COLORS) {
            ImageView colorView = new ImageView(this);
            colorView.setLayoutParams(mLayoutParams);
            colorView.setId(ix);
            GradientDrawable colorCircle = (GradientDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.color_circle);
            Drawable checkedColorCircle = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check_circle_black_24dp);
            assert colorCircle != null;
            if(ix == 0) {
                checkedColorCircle.setColorFilter(new BlendModeColorFilter(color, BlendMode.COLOR));
                colorView.setBackground(checkedColorCircle);
            } else {
                colorCircle.setColor(color);
                colorView.setBackground(colorCircle);
            }
            mPopupActivityBinding.colorSheet.addView(colorView);
            ix++;

            colorView.setOnClickListener(v -> {
                if(bookmarkColor == -1) { //초기
                    bookmarkColor = v.getId();
                    checkedColorCircle.setColorFilter(new BlendModeColorFilter(COLORS[bookmarkColor], BlendMode.COLOR));
                    colorView.setBackground(checkedColorCircle);
                } else {
                    if(bookmarkColor == v.getId()) { // 이미 눌러진거 다시 누름
                        colorCircle.setColor(COLORS[bookmarkColor]);
                        v.setBackground(colorCircle);
                        bookmarkColor = -1;
                    } else { // 새로운거 누름
                        colorCircle.setColor(COLORS[bookmarkColor]);
                        mPopupActivityBinding.colorSheet.getRootView().findViewById(bookmarkColor).setBackground(colorCircle);

                        bookmarkColor = v.getId();
                        checkedColorCircle.setColorFilter(new BlendModeColorFilter(COLORS[bookmarkColor], BlendMode.COLOR));
                        v.setBackground(checkedColorCircle);
                    }
                }
            });
        }

    }*/

}
