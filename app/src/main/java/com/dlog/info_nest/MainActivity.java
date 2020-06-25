package com.dlog.info_nest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dlog.info_nest.databinding.MainActivityBinding;
import com.dlog.info_nest.ui.home.HomeFragment;
import com.dlog.info_nest.ui.main.MainFragment;
import com.dlog.info_nest.ui.palette.PaletteFragment;
import com.dlog.info_nest.ui.palette.PaletteFragment2;
import com.dlog.info_nest.utilities.ClipboardService;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding mMainActivityBinding;
    private HomeFragment mHomeFragment;
    private MainFragment mMainFragment;
    private PaletteFragment mPaletteFragment;
    private FragmentManager mFragmentManager;
    /**
     * 백 버튼 두번 연속 누르면 앱 종료
     */
    private long backPressedTime = 0;
    private volatile Toast toastBackBt ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mFragmentManager = getSupportFragmentManager();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //clipboard service 등록
        Intent serviceIntent = new Intent(this, ClipboardService.class);
        startService(serviceIntent);


        // TODO DB에 저장된 북마크가 있으면 바로 homeFragment로 이동 없으면 initFragment로 이동

        if (savedInstanceState == null) {
            mHomeFragment = new HomeFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mHomeFragment).commit();
        }

        setBtnListener();

    }

    private void setBtnListener() {
        mMainActivityBinding.btnHome.setOnClickListener(v -> {
            if(mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, mHomeFragment).commit();
            }
            changeFragment(mHomeFragment, mMainFragment, mPaletteFragment);
            mMainActivityBinding.btnHome.setBackground(getResources().getDrawable(R.drawable.ic_home_gray_24dp));
            mMainActivityBinding.btnMain.setBackground(getResources().getDrawable(R.drawable.ic_view_day_black_24dp));
            mMainActivityBinding.btnPalette.setBackground(getResources().getDrawable(R.drawable.ic_palette_black_24dp));

        });

        mMainActivityBinding.btnMain.setOnClickListener(v -> {
            if (mMainFragment == null) {
                mMainFragment = new MainFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, mMainFragment).commit();
            }
            changeFragment(mMainFragment, mHomeFragment, mPaletteFragment);
            mMainActivityBinding.btnHome.setBackground(getResources().getDrawable(R.drawable.ic_home_black_24dp));
            mMainActivityBinding.btnMain.setBackground(getResources().getDrawable(R.drawable.ic_view_day_gray_24dp));
            mMainActivityBinding.btnPalette.setBackground(getResources().getDrawable(R.drawable.ic_palette_black_24dp));

        });

        mMainActivityBinding.btnPalette.setOnClickListener(v -> {
            if (mPaletteFragment == null) {
                mPaletteFragment = new PaletteFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, mPaletteFragment).commit();
            }
            changeFragment(mPaletteFragment, mHomeFragment, mMainFragment);
            mMainActivityBinding.btnHome.setBackground(getResources().getDrawable(R.drawable.ic_home_black_24dp));
            mMainActivityBinding.btnMain.setBackground(getResources().getDrawable(R.drawable.ic_view_day_black_24dp));
            mMainActivityBinding.btnPalette.setBackground(getResources().getDrawable(R.drawable.ic_palette_gray_24dp));
        });

    }

    private void changeFragment(Fragment showFragment, Fragment anotherFragment1, Fragment anotherFragment2) {

        if (showFragment != null) {
            mFragmentManager.beginTransaction().show(showFragment).commit();
        }
        if (anotherFragment1 != null) {
            mFragmentManager.beginTransaction().hide(anotherFragment1).commit();
        }
        if (anotherFragment2 != null) {
            mFragmentManager.beginTransaction().hide(anotherFragment2).commit();
        }
    }

    @Override
    public void onBackPressed() {
        ConstraintLayout main_drawer_layout = findViewById(R.id.main_drawer_layout);
        ConstraintLayout main_hide_layout = findViewById(R.id.main_hide_layout);
        if(main_drawer_layout != null) {
            if (main_drawer_layout.getVisibility() == View.VISIBLE && main_hide_layout.getVisibility() == View.VISIBLE) {
                main_drawer_layout.setVisibility(View.INVISIBLE);
                main_hide_layout.setVisibility(View.INVISIBLE);
            }
        }
        if(System.currentTimeMillis() > backPressedTime + 2000){
            backPressedTime = System.currentTimeMillis();
            toastBackBt = Toast.makeText(this," 뒤로 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG);
            toastBackBt.show();
            return;
        }
        if(System.currentTimeMillis() <= backPressedTime + 2000){
            finish();
            toastBackBt.cancel();
        }
    }
}

