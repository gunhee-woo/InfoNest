package com.dlog.info_nest.ui.palette.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dlog.info_nest.R;


public class HideEffectRightView extends LinearLayout {
    Context context;
    public HideEffectRightView(Context context){
        super(context);
        this.context = context;
        initView();
    }
    public HideEffectRightView(Context context, @Nullable AttributeSet attr){
        super(context, attr);
        this.context = context;
        initView();
    }
    public HideEffectRightView(Context context, @Nullable AttributeSet attr, int defstyleAttr){
        super(context, attr, defstyleAttr);
        this.context = context;
        initView();
    }
    private void initView(){
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)context.getSystemService(infService);
        View v = li.inflate(R.layout.hide_effect_right_view, this, false);
        addView(v);
    }
}
