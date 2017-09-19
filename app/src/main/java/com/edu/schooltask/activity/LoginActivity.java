package com.edu.schooltask.activity;

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

public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_vp) ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<>();
    private MenuItem registerItem;  //注册按钮

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    registerItem.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                if("注册".equals(registerItem.getTitle())){
                    viewPager.setCurrentItem(1);
                    setTitle("注册");
                    registerItem.setTitle("登录");
                    return true;
                }
                if("登录".equals(registerItem.getTitle())){
                    viewPager.setCurrentItem(0);
                    setTitle("登录");
                    registerItem.setTitle("注册");
                    return true;
                }
        }
        return true;
    }
}
