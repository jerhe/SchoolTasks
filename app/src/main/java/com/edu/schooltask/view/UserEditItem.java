package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UserEditItem extends RelativeLayout {
    TextView nameText;
    TextView textText;
    ImageView imageView;
    CircleImageView headView;
    public UserEditItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_user_edit_item, this);
        nameText = (TextView) findViewById(R.id.uei_name);
        textText = (TextView) findViewById(R.id.uei_text);
        imageView = (ImageView) findViewById(R.id.uei_image);
        headView = (CircleImageView) findViewById(R.id.uei_head);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserEditItem);
        String name = typedArray.getString(R.styleable.UserEditItem_name);
        String type = typedArray.getString(R.styleable.UserEditItem_type);
        typedArray.recycle();

        nameText.setText(name);
        if("text".equals(type)){
            textText.setVisibility(VISIBLE);
            imageView.setVisibility(GONE);
            headView.setVisibility(GONE);
        }
        if("image".equals(type)){
            textText.setVisibility(GONE);
            imageView.setVisibility(VISIBLE);
            headView.setVisibility(GONE);
        }
        if("head".equals(type)){
            textText.setVisibility(GONE);
            imageView.setVisibility(GONE);
            headView.setVisibility(VISIBLE);
        }
    }

    public void setTextText(String text){
        textText.setText(text);
    }

    public String getTextText(){
        return textText.getText().toString();
    }

    public ImageView getImageView(){
        return imageView;
    }

    public CircleImageView getHeadView(){
        return headView;
    }
}
