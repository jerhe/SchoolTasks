package com.edu.schooltask.view.useritem;

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
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.UserUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class UserItemDynamicView extends RelativeLayout {
    private CircleImageView headView;
    private TextView nameText;
    private TextView sexText;
    private TextView releaseTimeText;
    private TextView schoolText;



    public UserItemDynamicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_dynamic, this);

        headView = (CircleImageView) findViewById(R.id.ud_head);
        nameText = (TextView) findViewById(R.id.ud_name);
        sexText = (TextView) findViewById(R.id.ud_sex);
        releaseTimeText = (TextView) findViewById(R.id.ud_release_time);
        schoolText = (TextView) findViewById(R.id.ud_school);
    }

    //设置所有信息
    public void setAll(Dynamic dynamic){
        setReleaseTime(dynamic.getCreateTime());
        setSchool(dynamic.getSchool());
        if(dynamic.isAnonymous()){
            setAnonymous();
        }
        else{
            final UserInfo userInfo = dynamic.getUserInfo();
            setHead(userInfo.getHead());
            setName(userInfo.getName());
            //setSex(userInfo.getSex());
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

    }

    //设置头像
    public void setHead(String head){
        UserUtil.setHead(getContext(), head, headView);
    }

    public void setAnonymous(){
        UserUtil.setHead(getContext(), R.drawable.ic_anonymous, headView);
        setName("匿名");
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

}