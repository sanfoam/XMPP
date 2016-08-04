package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
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
    LinearLayout mLLBottom;

    @InjectView(R.id.main_tv_title)
    TextView mTVTitle;

    @InjectView(R.id.main_viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }


}
