package com.edu.schooltask.other;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by 夜夜通宵 on 2017/8/20.
 */

//自定义Span 用于去除下划线
public class CustomClickableSpan extends ClickableSpan {
    final int textColor = Color.parseColor("#1B9DFF");

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(textColor);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {

    }
}
