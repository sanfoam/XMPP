package com.jxust.asus.xmpp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.fragment.ContactsFragment;
import com.jxust.asus.xmpp.fragment.SessionFragment;
import com.jxust.asus.xmpp.utils.ToolBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends FragmentActivity {

    // xUtils 中的viewUtils 注解方式去找控件
    // xUtils包括viewUtils,httpUtils,dbUtils,bitmapUtils
    // ButterKnife.jar只集成了xUtils中viewUtils的功能
    @InjectView(R.id.main_tv_title)
    TextView mMainTitle;
    @InjectView(R.id.main_viewpager)
    ViewPager mMainViewPager;
    @InjectView(R.id.main_bottom)
    LinearLayout mMainBottom;

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ToolBarUtil mToolBarUtil;
    private String[] mToolBarTitleArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initData();
        initListener();
    }

    private void initListener() {
        mMainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 修改颜色
                mToolBarUtil.changeColor(position);
                // 修改title
                mMainTitle.setText(mToolBarTitleArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mToolBarUtil.setOnToolBarClickListener(new ToolBarUtil.OnToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                mMainViewPager.setCurrentItem(position);
            }
        });
    }

    private void initData() {
        //viewPager-->view-->pagerAdapter
        //viewPager-->fragment-->fragmentPagerAdapter-->fragment数量比较少
        //viewPager-->fragment-->fragmentStatePagerAdapter

        // 添加fragment到集合中
        mFragments.add(new SessionFragment());
        mFragments.add(new ContactsFragment());

        mMainViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // 底部的按钮
        mToolBarUtil = new ToolBarUtil();

        // 文字的内容
        mToolBarTitleArr = new String[]{"会话", "联系人"};

        // 图标内容
        int[] iconArr = {R.drawable.selector_message, R.drawable.selector_selfinfo};

        mToolBarUtil.createToolBar(mMainBottom, mToolBarTitleArr, iconArr);

        // 设置默认选中会话
        mToolBarUtil.changeColor(0);     // 默认选中第一个控件
    }


    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
