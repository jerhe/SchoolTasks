package com.edu.schooltask.ui.view.Inputtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public class InputTextNameView extends AppCompatTextView {
    Paint paint;
    RectF rect;
    float radius = 10;

    public InputTextNameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rect, radius, radius, paint);
        rect.set(getWidth() - radius, 0, getWidth(), getHeight());
        canvas.drawRect(rect, paint);
        super.onDraw(canvas);

    }

    public void setBackground(int color){
        paint.setColor(color);
        invalidate();
    }
}
