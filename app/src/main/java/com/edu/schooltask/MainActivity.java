package com.edu.schooltask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import adapter.ViewPagerAdapter;
import base.BaseActivity;
import beans.User;
import fragment.main.HomeFragment;
import fragment.main.OrderFragment;
import fragment.main.TalkFragment;
import fragment.main.UserFragment;
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
        viewPager.setOffscreenPageLimit(4); //设置预加载页面数
    }

    private void initBottomMenu(){
        bottomMenu.setOnMenuSelectedListener(new BottomMenu.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }
        });
        bottomMenu.setPagePosition(0);
    }

    /**
     * 得到本地用户
     */
    private void getUser(){
        List<User> users = DataSupport.findAll(User.class);
        if(users.size() == 1){
            user = users.get(0);
        }
        else{
            user = null;
        }
    }

    private void updateUser(){
        if(userFragment != null) ((UserFragment)userFragment).updateUser(user);  //传入User到用户页
        if(homeFragment != null) ((HomeFragment)homeFragment).updateUser(user);  //传入User到首页
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUser();  //每次回到主页更新用户信息
        updateUser();
    }
}
