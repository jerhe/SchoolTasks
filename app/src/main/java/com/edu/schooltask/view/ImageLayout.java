package com.edu.schooltask.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by 夜夜通宵 on 2017/9/10.
 */

public class ImageLayout extends RelativeLayout {

    float widthPercent = 1;
    float heightPercent = 1;

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.AT_MOST);
        heightMeasureSpec = (int)(widthMeasureSpec / widthPercent * heightPercent);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setPercentage(float widthPercent, float heightPercent){
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }
}
