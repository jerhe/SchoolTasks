package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.utils.DensityUtil;

/**
 * Created by 夜夜通宵 on 2017/9/11.
 */

public class ImageTextView extends RelativeLayout {

    private ImageView iconLeftView;
    private TextView textView;
    private ImageView iconRightView;
    private int gravity;

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_image_text, this);
        iconLeftView = (ImageView) findViewById(R.id.it_icon_left);
        iconRightView = (ImageView) findViewById(R.id.it_icon_right);
        textView = (TextView) findViewById(R.id.it_text);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        gravity = typedArray.getInteger(R.styleable.ImageTextView_ImageText_gravity, 0);
        textView.setTextColor(typedArray.getColor(R.styleable.ImageTextView_ImageText_textColor,
                getResources().getColor(R.color.fontColor)));
        setText(typedArray.getString(R.styleable.ImageTextView_ImageText_text));
        setIcon(typedArray.getResourceId(R.styleable.ImageTextView_ImageText_icon, 0));
        typedArray.recycle();
        if(gravity == 0) {
            iconLeftView.setVisibility(VISIBLE);
            iconRightView.setVisibility(GONE);
        }
        else{
            iconLeftView.setVisibility(GONE);
            iconRightView.setVisibility(VISIBLE);
        }
    }

    public void setText(String text){
        textView.setText(text);
    }

    public void setTextColor(int color){
        textView.setTextColor(color);
    }

    public void startIconAnimation(Animation animation){
        if(gravity == 0) iconLeftView.startAnimation(animation);
        else iconRightView.startAnimation(animation);
    }

    public void setIcon(int resId){
        if (resId == 0) return;
        if(gravity == 0) iconLeftView.setImageResource(resId);
        else iconRightView.setImageResource(resId);
    }

    @Override
    public ViewPropertyAnimator animate(){
        if(gravity == 0) return iconLeftView.animate();
        if(gravity == 1) return iconRightView.animate();
        return animate();
    }


}
