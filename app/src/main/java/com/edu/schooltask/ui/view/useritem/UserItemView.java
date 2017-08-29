package com.edu.schooltask.ui.view.useritem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.utils.UserUtil;

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

    public void setHead(UserInfo userInfo){
        UserUtil.setHead(getContext(), userInfo, headView);
    }

    public void setAll(UserInfo user){
        setHead(user);
        setName(user.getName());
        setSign(user.getSign());
    }
}
