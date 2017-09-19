package com.edu.schooltask.view.useritem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.UserUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class UserItemTaskView extends RelativeLayout {
    private CircleImageView headView;
    private TextView nameText;
    private TextView sexText;
    private TextView releaseTimeText;
    private TextView schoolText;
    private TextView desText;



    public UserItemTaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_task, this);

        headView = (CircleImageView) findViewById(R.id.ut_head);
        nameText = (TextView) findViewById(R.id.ut_name);
        sexText = (TextView) findViewById(R.id.ut_sex);
        releaseTimeText = (TextView) findViewById(R.id.ut_release_time);
        schoolText = (TextView) findViewById(R.id.ut_school);
        desText = (TextView) findViewById(R.id.ut_des);
    }

    //设置所有信息
    public void setAll(final UserInfo userInfo, String releaseTime, String school, String des, boolean event){
        setHead(userInfo);
        setName(userInfo.getName());
        //setSex(userInfo.getSex());
        setReleaseTime(releaseTime, event);
        setSchool(school);
        setDes(des);
        //点击跳转到用户主页
        if(event)
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
    public void setReleaseTime(String releaseTime, boolean detail){
        if(detail)
            releaseTimeText.setText(DateUtil.getDetailTime(releaseTime));
        else
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
