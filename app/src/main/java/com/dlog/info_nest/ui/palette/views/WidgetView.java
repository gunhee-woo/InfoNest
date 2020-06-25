package com.dlog.info_nest.ui.palette.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dlog.info_nest.R;

public class WidgetView extends LinearLayout {
    private int layoutResId;
    private String colorId;
    private String title;
    private String url;

    /**
     * 이 figureView가 속한 gridlayout의 사각형 이미지 뷰의 위치
     *
     *     0 1 2
     *     3 4 5
     *     6 7 8
     */
    private int position = -1;

    View v;
    public WidgetView(Context context, String title, String url, int position) {
        super(context);
        this.title = title;
        this.url = url;
        this.position = position;
        initView();
    }
    public WidgetView(Context context, @Nullable AttributeSet attr){
        super(context, attr);
        initView();
    }
    public WidgetView(Context context, @Nullable AttributeSet attr, int defStyleAttr){
        super(context, attr, defStyleAttr);
        initView();
    }
    private void initView(){
        this.removeAllViews();
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        v = li.inflate(R.layout.widgetview,this,false);
        getChildTextView().setText(title);
        addView(v);
    }
    public TextView getChildTextView() { return v.findViewById(R.id.txt_figure);}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public String getColorId() {
        return colorId;
    }

    public void setTitle(String title) {
        this.title = title;
        initView();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
        initView();
    }

    public void setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
        initView();
    }
}