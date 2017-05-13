package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.view.ViewPagerTab;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity {
    private AppBarLayout topLayout;
    private Toolbar toolbar;
    private TextView titleText;
    private ImageView headImage;
    private TextView nameText;
    private ViewPagerTab tab;
    private ViewPager viewPager;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        topLayout = (AppBarLayout) findViewById(R.id.user_abl);
        titleText = (TextView) findViewById(R.id.toolbar_name);
        headImage = (ImageView) findViewById(R.id.user_head);
        nameText = (TextView) findViewById(R.id.user_name);
        tab = (ViewPagerTab) findViewById(R.id.user_tab);
        viewPager = (ViewPager) findViewById(R.id.user_vp);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tab.addTab("XXX");
        tab.addTab("XXX");
        tab.addTab("个人信息");
        tab.setViewPager(viewPager);

        topLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha = (float)-verticalOffset / (topLayout.getHeight() - titleText.getHeight());
                titleText.setAlpha(alpha > 0.5 ? alpha : 0);
                toolbar.setAlpha(alpha);
            }
        });

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("user");
        titleText.setText(user.getName());
    }
}
