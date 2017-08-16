package com.edu.schooltask.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.activity.PrivateMessageActivity;
import com.edu.schooltask.activity.TaskOrderActivity;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.utils.GlideUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public class UserItemView extends RelativeLayout {
    private CircleImageView headView;
    private TextView nameText;
    private TextView signText;

    public UserItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_item, this);

        headView = (CircleImageView) findViewById(R.id.ui_head);
        nameText = (TextView) findViewById(R.id.ui_name);
        signText = (TextView) findViewById(R.id.ui_sign);
    }

    public void setName(String name){
        nameText.setText(name);
    }

    public void setSign(String sign){
        signText.setText(sign);
    }

    public void setHead(String userId){
        GlideUtil.setHead(getContext(), userId, headView, true);
    }

    public void setAll(final UserInfoBase user){
        setHead(user.getUserId());
        setName(user.getName());
        setSign(user.getSign());
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent talkIntent = new Intent(getContext(), PrivateMessageActivity.class);
                talkIntent.putExtra("user", user);
                getContext().startActivity(talkIntent);
            }
        });
    }
}
