package com.edu.schooltask.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/7.
 */

public class PayPasswordInputBoard extends LinearLayout {

    private TextView btnBack;
    private List<TextView> btns = new ArrayList<>();

    private InputTextView passwordView;
    Animation showAnimation;
    Animation hideAnimation;

    public PayPasswordInputBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_pay_password_input_board, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        btns.add((TextView) findViewById(R.id.ppib_0));
        btns.add((TextView) findViewById(R.id.ppib_1));
        btns.add((TextView) findViewById(R.id.ppib_2));
        btns.add((TextView) findViewById(R.id.ppib_3));
        btns.add((TextView) findViewById(R.id.ppib_4));
        btns.add((TextView) findViewById(R.id.ppib_5));
        btns.add((TextView) findViewById(R.id.ppib_6));
        btns.add((TextView) findViewById(R.id.ppib_7));
        btns.add((TextView) findViewById(R.id.ppib_8));
        btns.add((TextView) findViewById(R.id.ppib_9));
        btnBack = (TextView) findViewById(R.id.ppib_back);
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_bottom_in);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_bottom_out);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        hide();
                        return true;
                    }
                }
                return false;
            }
        });

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = btns.indexOf(v);
                if(passwordView != null) passwordView.inputPayPassword(index);
            }
        };
        for(TextView btn : btns){
            btn.setOnClickListener(listener);
        }

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordView != null) passwordView.deletePayPassword();
            }
        });
    }

    public void show(){
        if (isShown()) return;
        requestFocus();
        setVisibility(VISIBLE);
        startAnimation(showAnimation);
    }

    public void hide(){
        if(!isShown()) return;
        setVisibility(GONE);
        startAnimation(hideAnimation);
    }

    public void setPasswordView(InputTextView inputTextView){
        inputTextView.setPayPasswordInputBoard(this);
        this.passwordView = inputTextView;
    }
}
