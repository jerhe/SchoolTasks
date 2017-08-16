package com.edu.schooltask.view.Inputtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public class InputTextTextView extends AppCompatEditText {
    Paint paint;
    RectF rect;
    int radius = 20;

    public InputTextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.lineColor));
        paint.setStyle(Paint.Style.STROKE);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        paint.setStrokeWidth(2f);
        //上下线
        canvas.drawLine(0, 0, width - radius / 2, 0, paint);
        canvas.drawLine(0, height, width - radius / 2, height, paint);
        //右线
        canvas.drawLine(width, radius / 2, width, height - radius / 2, paint);
        //右上弧
        paint.setStrokeWidth(1f);
        rect.set(width - radius, 0, width, radius);
        canvas.drawArc(rect, 270, 90, false, paint);
        //右下弧
        rect.set(width - radius, height - radius, width, height);
        canvas.drawArc(rect, 0, 90, false, paint);


    }

    public void setBackground(int color){
        paint.setColor(color);
        invalidate();
    }
}
