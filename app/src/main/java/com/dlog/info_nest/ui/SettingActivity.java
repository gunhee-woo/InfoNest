package com.dlog.info_nest.ui;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.SettingActivityBinding;

import java.util.ArrayList;

import static com.dlog.info_nest.ui.main.MainFragment.hashingPassword;

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

        mSettingActivityBinding.settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) { // 비밀번호 설정
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    final EditText editText = new EditText(getApplicationContext());
                    builder.setView(editText);
                    builder.setMessage("비밀번호를 입력해 주세요");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = editText.getText().toString();
                            if(password.length() < 4 || password.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "비밀번호를 4자이상 입력해주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                if(BasicApp.prefs.getPasswordPreferences().equals(hashingPassword(password))) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SettingActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                    builder2.setMessage("비밀번호 변경");
                                    final EditText editText1 = new EditText(getApplicationContext());
                                    builder2.setView(editText1);
                                    builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String password = editText1.getText().toString();
                                            BasicApp.prefs.savePreferences(hashingPassword(password));
                                            Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder2.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    builder2.setCancelable(false).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
                    builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.setCancelable(false).create();
                    alertDialog.show();
                }
            }
        });
    }
}
