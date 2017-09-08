package com.edu.schooltask.ui.activity;

import android.os.Bundle;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;

import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }
}
