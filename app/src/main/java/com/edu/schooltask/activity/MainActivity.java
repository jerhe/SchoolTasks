package com.edu.schooltask.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.fragment.main.HomeFragment;
import com.edu.schooltask.fragment.main.OrderFragment;
import com.edu.schooltask.fragment.main.TalkFragment;
import com.edu.schooltask.fragment.main.UserFragment;
import com.edu.schooltask.view.BottomMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.login.UnloginEvent;
import server.api.token.RefreshTokenEvent;
import server.api.token.TokenErrorEvent;

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
        EventBus.getDefault().register(this);
        mDataCache = new DataCache(this);
        new SchoolTask(mDataCache);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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


    //未登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnlogin(UnloginEvent event){
        toastShort("请先登录");
        openActivity(LoginActivity.class);
    }

    //Token错误
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenError(TokenErrorEvent event){
        mDataCache.removeUser();
        remainHome();   //关闭主页以外所有活动
        EventBus.getDefault().post(new LogoutEvent());
        toastShort("账号异常，请重新登录");
        openActivity(LoginActivity.class);
    }

    //刷新Token
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshToken(RefreshTokenEvent event){
        mDataCache.refreshToken(event.getToken());
    }
}
