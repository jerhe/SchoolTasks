package com.edu.schooltask.ui.view.useritem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.ui.activity.UserActivity;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class UserItemReleaseView extends RelativeLayout {
    private CircleImageView headView;
    private TextView nameText;
    private TextView sexText;
    private TextView releaseTimeText;
    private TextView schoolText;
    private TextView desText;



    public UserItemReleaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_release, this);

        headView = (CircleImageView) findViewById(R.id.ur_head);
        nameText = (TextView) findViewById(R.id.ur_name);
        sexText = (TextView) findViewById(R.id.ur_sex);
        releaseTimeText = (TextView) findViewById(R.id.ur_release_time);
        schoolText = (TextView) findViewById(R.id.ur_school);
        desText = (TextView) findViewById(R.id.ur_des);
    }

    //设置所有信息
    public void setAll(final UserInfo userInfo, String releaseTime, String school,
                       String des, boolean event){
        setHead(userInfo.getUserId());
        setName(userInfo.getName());
        setSex(userInfo.getSex());
        setReleaseTime(releaseTime);
        setSchool(school);
        setDes(des);
        //点击跳转到用户主页
        if(event)
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserActivity.class);
                    intent.putExtra("userId", userInfo.getUserId());
                    getContext().startActivity(intent);
                }
            });

    }

    //设置头像
    public void setHead(String userId){
        GlideUtil.setHead(getContext(), userId, headView);
    }

    //设置昵称
    public void setName(String name){
        nameText.setText(name);
    }

    //设置性别
    public void setSex(int sex){
        switch (sex){
            case 0:
                sexText.setText("♂");
                sexText.setTextColor(Color.parseColor("#1B9DFF"));
                break;
            case 1:
                sexText.setText("♀");
                sexText.setTextColor(Color.parseColor("#FF3333"));
                break;
            default:
                sexText.setText("");
        }
    }

    //设置发布时间
    public void setReleaseTime(String releaseTime){
        releaseTimeText.setText(DateUtil.getLong(DateUtil.stringToCalendar(releaseTime)));
    }

    //设置学校
    public void setSchool(String school){
        schoolText.setText(school);
    }

    //设置分类
    public void setDes(String des){
        desText.setText(des);
    }
}
