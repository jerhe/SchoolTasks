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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/9/7.
 */

public class PayView extends RelativeLayout{

    private TextView titleText;
    private InputTextView payPasswordText;
    private PayPasswordInputBoard inputBoard;
    private View shadowView;
    private LinearLayout payLayout;

    Animation showAnimation;
    Animation shadowAnimation;

    public PayView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_pay, this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_bottom_in);
        shadowAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        titleText = (TextView) findViewById(R.id.pay_title);
        payPasswordText = (InputTextView) findViewById(R.id.pay_pwd);
        inputBoard = (PayPasswordInputBoard) findViewById(R.id.pay_ppib);
        shadowView = findViewById(R.id.pay_shadow);
        payLayout = (LinearLayout) findViewById(R.id.pay_layout);

        shadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

        inputBoard.setPasswordView(payPasswordText);
    }

    public void setTitle(String title){
        titleText.setText(title);
    }

    public void setPayPasswordFinishedListener(PayPasswordView.PayPasswordFinishedListener listener){
        payPasswordText.setPayPasswordFinishedListener(listener);
    }

    public void show(){
        clearPassword();
        setVisibility(VISIBLE);
        requestFocus();
        payLayout.startAnimation(showAnimation);
        shadowView.startAnimation(shadowAnimation);
    }

    public void hide(){
        setVisibility(GONE);
    }

    public void clearPassword(){
        payPasswordText.clearPayPassword();
    }
}
