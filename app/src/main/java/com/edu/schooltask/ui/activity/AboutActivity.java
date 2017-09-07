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

public class AboutActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }
}
