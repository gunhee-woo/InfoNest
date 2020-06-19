package com.dlog.info_nest.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.SettingActivityBinding;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private SettingActivityBinding mSettingActivityBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingActivityBinding = DataBindingUtil.setContentView(this, R.layout.setting_activity);

        ArrayList<SettingListItem> settingListItems = new ArrayList<>();
        settingListItems.add(new SettingListItem("비밀번호 설정", null));
        settingListItems.add(new SettingListItem("서비스 이용약관", null));
        settingListItems.add(new SettingListItem("버전 정보", "ver1.0"));
        settingListItems.add(new SettingListItem("개발자 메일 주소", "dlog@dlog.com"));

        SettingListAdapter settingListAdapter = new SettingListAdapter(settingListItems);
        mSettingActivityBinding.settingListView.setAdapter(settingListAdapter);

        setBtnListener();
    }

    public void setBtnListener() {
        mSettingActivityBinding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
