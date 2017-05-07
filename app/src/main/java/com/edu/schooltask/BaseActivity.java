package com.edu.schooltask;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView titleText;
    public static User user = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        View v = findViewById(R.id.toolbar);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
        if(v!=null){
            toolbar = (Toolbar)v;
            setSupportActionBar(toolbar);
            titleText = (TextView)v.findViewById(R.id.toolbar_title);
            if(titleText!=null){
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置返回按钮
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {   //返回按钮事件
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if(titleText!=null) titleText.setText(title);
    }

}
