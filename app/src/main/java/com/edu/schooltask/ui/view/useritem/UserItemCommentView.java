package com.edu.schooltask.ui.view.useritem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.UserActivity;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/20.
 */

public class UserItemCommentView extends RelativeLayout{
    private CircleImageView headView;
    private TextView nameText;
    private TextView schoolText;
    private TextView commentTimeText;
    private TextView sexText;

    public UserItemCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_comment, this);
        headView = (CircleImageView)findViewById(R.id.cui_head);
        nameText = (TextView)findViewById(R.id.cui_name);
        schoolText = (TextView)findViewById(R.id.cui_school);
        commentTimeText = (TextView) findViewById(R.id.cui_release_time);
        sexText = (TextView) findViewById(R.id.cui_sex);
    }

    //设置所有信息
    public void setAll(String userId, final String name, int sex, String commentTime, String school){
        setHead(userId);
        setName(name);
        setSex(sex);
        setCommentTime(commentTime);
        setSchool(school);
        //点击跳转到用户主页
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("name", name);
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

    //设置评论时间
    public void setCommentTime(String commentTime){
        commentTimeText.setText(DateUtil.getLong(DateUtil.stringToCalendar(commentTime)));
    }

    //设置学校
    public void setSchool(String school){
        schoolText.setText(school);
    }
}
