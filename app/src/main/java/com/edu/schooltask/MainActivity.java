package com.edu.schooltask;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import adapter.ViewPagerAdapter;
import fragment.HomeFragment;
import fragment.OrderFragment;
import fragment.TalkFragment;
import fragment.UserFragment;
import view.BottomMenu;

public class MainActivity extends BaseActivity {
    ViewPager viewPager;
    List<Fragment> fragments = new ArrayList<>();
    Fragment homeFragment;
    Fragment talkFragment;
    Fragment orderFragment;
    Fragment userFragment;
    BottomMenu bottomMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        initViewPager();
        initBottomMenu();
    }

    private void bind(){
        viewPager = (ViewPager)findViewById(R.id.main_vp);
        bottomMenu = (BottomMenu)findViewById(R.id.main_bm);
        //
    }

    private void initViewPager(){
        homeFragment = new HomeFragment();
        talkFragment = new TalkFragment();
        orderFragment = new OrderFragment();
        userFragment = new UserFragment();
        fragments.add(homeFragment);
        fragments.add(talkFragment);
        fragments.add(orderFragment);
        fragments.add(userFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                bottomMenu.setPagePosition(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initBottomMenu(){
        bottomMenu.setOnMenuSelectedListener(new BottomMenu.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }
        });
        bottomMenu.setPagePosition(0);
        bottomMenu.setMessageNum(4);
    }

}