package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/8.
 */

public class MenuItem extends RelativeLayout{
    private View line1;
    private View line2;
    private View line3;
    private View line4;
    private TextView text;

    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_menu_item,this);
        line1 = findViewById(R.id.mi_line1);
        line2 = findViewById(R.id.mi_line2);
        line3 = findViewById(R.id.mi_line3);
        line4 = findViewById(R.id.mi_line4);
        text = (TextView) findViewById(R.id.mi_text);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.MenuItem);
        text.setText(typedArray.getString(R.styleable.MenuItem_itemText));
        setNormal();
    }

    public void setLight(){
        line1.setBackgroundColor(Color.parseColor("#1B9DFF"));
        line2.setBackgroundColor(Color.parseColor("#1B9DFF"));
        line3.setBackgroundColor(Color.parseColor("#1B9DFF"));
        line4.setBackgroundColor(Color.parseColor("#1B9DFF"));
        text.setTextColor(Color.parseColor("#1B9DFF"));
    }

    public void setNormal(){
        line1.setBackgroundColor(Color.parseColor("#AAAAAA"));
        line2.setBackgroundColor(Color.parseColor("#AAAAAA"));
        line3.setBackgroundColor(Color.parseColor("#AAAAAA"));
        line4.setBackgroundColor(Color.parseColor("#AAAAAA"));
        text.setTextColor(Color.parseColor("#AAAAAA"));
    }
}
