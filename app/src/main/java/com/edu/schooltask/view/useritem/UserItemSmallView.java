package com.edu.schooltask.view.useritem;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.utils.UserUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class UserItemSmallView extends LinearLayout {
    private CircleImageView headView;
    private TextView nameText;



    public UserItemSmallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_small, this);
        headView = (CircleImageView) findViewById(R.id.us_head);
        nameText = (TextView) findViewById(R.id.us_name);
    }

    //设置所有信息
    public void setAll(final UserInfo userInfo){
        setHead(userInfo);
        setName(userInfo.getName());
        //点击跳转到用户主页
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("name", userInfo.getName());
                getContext().startActivity(intent);
            }
        });

    }

    //设置头像
    public void setHead(UserInfo userInfo){
        UserUtil.setHead(getContext(), userInfo.getHead(), headView);
    }

    //设置昵称
    public void setName(String name){
        nameText.setText(name);
    }
}