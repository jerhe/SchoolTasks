package com.edu.schooltask;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import adapter.ViewPagerAdapter;
import base.BaseActivity;
import fragment.login.LoginFragment;
import fragment.login.RegisterInfoFragment;
import fragment.login.RegisterPhoneFragment;

public class LoginActivity extends BaseActivity {
    private ViewPager viewPager;
    List<Fragment> fragments = new ArrayList<>();
    private Fragment loginFragment;
    private Fragment registerPhoneFragment;
    private Fragment registerInfoFragment;
    private MenuItem registerItem;
    public String registerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登录");
        viewPager = (ViewPager) findViewById(R.id.login_vp);
        initViewPager();
    }

    private void initViewPager(){
        loginFragment = new LoginFragment();
        registerPhoneFragment = new RegisterPhoneFragment(viewPager);
        registerInfoFragment = new RegisterInfoFragment();
        fragments.add(loginFragment);
        fragments.add(registerPhoneFragment);
        fragments.add(registerInfoFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {   //禁止滑动
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register,menu);
        registerItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_register:
                viewPager.setCurrentItem(1);
                setTitle("注册");
                registerItem.setVisible(false);
                break;
        }
        return true;
    }
}
