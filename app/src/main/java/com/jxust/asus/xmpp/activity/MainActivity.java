package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {

    // xUtils 中的viewUtils 注解方式去找控件
    // xUtils包括viewUtils,httpUtils,dbUtils,bitmapUtils
    // ButterKnife.jar只集成了xUtils中viewUtils的功能

    @InjectView(R.id.main_bottom)
    LinearLayout mMainBottom;

    @InjectView(R.id.main_tv_title)
    TextView mTVTitle;

    @InjectView(R.id.main_viewpager)
    ViewPager mMainViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        //viewPager-->view-->pagerAdapter
        //viewPager-->fragment-->fragmentPagerAdapter-->fragment数量比较少
        //viewPager-->fragment-->fragmentStatePagerAdapter
//        mMainViewPager.setAdapter();
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
