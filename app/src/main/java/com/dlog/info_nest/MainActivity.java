package com.dlog.info_nest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.dlog.info_nest.databinding.MainActivityBinding;
import com.dlog.info_nest.ui.home.HomeFragment;
import com.dlog.info_nest.ui.main.MainFragment;
import com.dlog.info_nest.ui.palette.PaletteFragment;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding mMainActivityBinding;
    private HomeFragment mHomeFragment;
    private MainFragment mMainFragment;
    private PaletteFragment mPaletteFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mFragmentManager = getSupportFragmentManager();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
        if(main_drawer_layout.getVisibility() == View.VISIBLE && main_hide_layout.getVisibility() == View.VISIBLE) {
            main_drawer_layout.setVisibility(View.INVISIBLE);
            main_hide_layout.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}

