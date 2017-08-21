package com.edu.schooltask.ui.view.Inputtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public class InputTextTextView extends AppCompatEditText {
    Paint paint;
    RectF rect;
    int radius = 10;

    public InputTextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        rect.set(0, 0, width, height);
        canvas.drawRoundRect(rect, radius, radius, paint);
        rect.set(0, 0, radius, height);
        canvas.drawRect(rect, paint);
        super.onDraw(canvas);
    }

    public void setBackground(int color){
        paint.setColor(color);
        invalidate();
    }
}
