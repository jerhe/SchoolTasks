package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.utils.AnimationUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class SelectText extends LinearLayout {
    TextView nameText;
    MaterialSpinner spinner;

    public SelectText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_select_text,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SelectText);
        String text = typedArray.getString(R.styleable.SelectText_ST_text);
        nameText = (TextView) findViewById(R.id.st_name);
        spinner = (MaterialSpinner) findViewById(R.id.st_spinner);
        nameText.setText(text);
    }

    public void setItems(String...items){
        spinner.setItems(items);
    }

    public String getText(){
        return spinner.getText().toString();
    }
}
