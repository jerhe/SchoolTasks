package com.edu.schooltask.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/8/23.
 */

public class ViewPagerTabItem extends RelativeLayout {

    TextView tabLabel;
    View tipView;

    public ViewPagerTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_viewpager_tab_item, this);
        tabLabel = (TextView) findViewById(R.id.vpti_label);
        tipView = findViewById(R.id.vpti_tip);
    }

    public void setText(String text){
        tabLabel.setText(text);
    }

    public void setTextColor(int color){
        tabLabel.setTextColor(color);
    }

    public void setTip(boolean show){
        tipView.setVisibility(show ? VISIBLE : INVISIBLE);
    }
}
