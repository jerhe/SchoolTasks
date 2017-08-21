package com.edu.schooltask.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.PrivateMessageActivity;
import com.edu.schooltask.ui.activity.UserActivity;
import com.edu.schooltask.data.DataCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class BaseActivity extends AppCompatActivity {
    protected static List<Activity> activities = new ArrayList<>();
    protected Toolbar toolbar;
    protected TextView titleText;
    protected Toast toast;
    protected static DataCache mDataCache;

    public static int foregroundCount = 0;  //前台Activity计数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mDataCache == null) mDataCache = new DataCache(this);
        activities.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        foregroundCount ++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foregroundCount --;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initActionBar();
        initStateBar();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if(titleText!=null) titleText.setText(title);
    }

    //-----------------------------------------------------------

    protected void initStateBar(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
    }

    protected void initActionBar(){
        View v = findViewById(R.id.toolbar);
        initStateBar();
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

    public static DataCache getDataCache(){
        return mDataCache;
    }

    //判断应用是否在前台
    public static boolean isForeground(){
        return foregroundCount > 0;
    }

    //-----------------------------------------------------------

    protected <T extends View> T getView(int resId){
        return (T) findViewById(resId);
    }

    public static void remainHome(){
        for(int i=activities.size()-1; i>0; i--){
            activities.get(i).finish();
        }
    }

    public static void destroyUserActivity(){
        for(int i=activities.size()-1; i>0; i--){
            Activity activity = activities.get(i);
            if(activity.getClass().equals(UserActivity.class)) activity.finish();
        }
    }

    public static void destroyPrivateMessageActivity(){
        for(int i=activities.size()-1; i>0; i--){
            Activity activity = activities.get(i);
            if(activity.getClass().equals(PrivateMessageActivity.class)) activity.finish();
        }
    }

    public void openActivity(Class cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void toast(final String text, final int duration){
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), text, duration);
                    } else {
                        toast.setText(text);
                        toast.setDuration(duration);
                    }
                    toast.show();
                }
            });
        }
    }

    public void toastShort(String text){
        toast(text, Toast.LENGTH_SHORT);
    }

    public void toastLong(String text){
        toast(text, Toast.LENGTH_LONG);
    }

}
