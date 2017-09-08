package com.edu.schooltask.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edu.schooltask.R;
import com.edu.schooltask.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/7.
 */

public class PayPasswordView extends LinearLayout {

    PayPasswordFinishedListener listener;

    private List<ImageView> texts = new ArrayList<>();
    private int[] password;
    private int index;

    public PayPasswordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_pay_password, this);
        texts.add((ImageView) findViewById(R.id.pp_text1));
        texts.add((ImageView) findViewById(R.id.pp_text2));
        texts.add((ImageView) findViewById(R.id.pp_text3));
        texts.add((ImageView) findViewById(R.id.pp_text4));
        texts.add((ImageView) findViewById(R.id.pp_text5));
        texts.add((ImageView) findViewById(R.id.pp_text6));
        password = new int[6];
    }

    public void input(int x){
        if(x < 0 || x > 9) return;
        password[index] = x;
        texts.get(index).setImageResource(R.drawable.ic_password);
        index ++;
        if (index > 5)
            if (listener != null)
                listener.onFinished(StringUtil.toString(password));
    }

    public String get(){
        return StringUtil.toString(password);
    }

    public void delete(){
        if(index != 0) index --;
        password[index] = 0;
        texts.get(index).setImageDrawable(null);
    }

    public void clear(){
        index = 0;
        for(ImageView text : texts){
            text.setImageDrawable(null);
        }
    }

    public void setPayPasswordFinishedListener(PayPasswordFinishedListener listener){
        this.listener = listener;
    }

    public interface PayPasswordFinishedListener{
        void onFinished(String password);
    }
}
