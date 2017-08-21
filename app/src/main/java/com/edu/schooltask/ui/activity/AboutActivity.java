package com.edu.schooltask.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.SchoolAdapter;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.MyScrollView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements MyScrollView.OnScrollListener{
    @BindView(R.id.about_sv)
    MyScrollView scrollView;
    @BindView(R.id.about_rv)
    RecyclerView recyclerView;
    @BindView(R.id.about_tab_layout)
    LinearLayout tabLayout;
    @BindView(R.id.about_tab_layout_2)
    LinearLayout tabLayout2;
    @BindView(R.id.about_head)
    View head;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        List<String> school = mDataCache.getSchool().subList(10,100);
        SchoolAdapter adapter = new SchoolAdapter(R.layout.item_school, school);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        view = new View(this, null);
        view.setBackgroundColor(Color.WHITE);
        scrollView.setOnScrollListener(this);
    }

    @Override
    public void onScroll(int scrollY) {
        if(scrollY >= head.getHeight()){
            if (view.getParent() != tabLayout2) {
                tabLayout.removeView(view);
                tabLayout2.addView(view);
                tabLayout2.setVisibility(View.VISIBLE);
            }
        }else{
            if (view.getParent() != tabLayout) {
                tabLayout2.removeView(view);
                tabLayout.addView(view);
                tabLayout2.setVisibility(View.INVISIBLE);
            }
        }
    }
}
