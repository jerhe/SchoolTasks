package com.edu.schooltask.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.fragment.login.LoginFragment;
import com.edu.schooltask.fragment.login.RegisterInfoFragment;
import com.edu.schooltask.fragment.login.RegisterPhoneFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_vp) ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<>();
    private MenuItem registerItem;  //注册按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle("登录");
        initViewPager();
    }

    private void initViewPager(){
        Fragment loginFragment = new LoginFragment();
        Fragment registerPhoneFragment = new RegisterPhoneFragment(viewPager);
        Fragment registerInfoFragment = new RegisterInfoFragment();
        fragments.add(loginFragment);
        fragments.add(registerPhoneFragment);
        fragments.add(registerInfoFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter<>(getSupportFragmentManager(), fragments);
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
        getMenuInflater().inflate(R.menu.register, menu);
        registerItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_register:    //点击注册按钮则隐藏
                viewPager.setCurrentItem(1);
                setTitle("注册");
                registerItem.setVisible(false);
        }
        return true;
    }
}
