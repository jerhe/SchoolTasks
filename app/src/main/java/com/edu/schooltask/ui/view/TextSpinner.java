package com.edu.schooltask.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class TextSpinner extends RelativeLayout {
    TextView nameText;
    MaterialSpinner spinner;

    public TextSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_text_spinner,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TextSpinner);
        String text = typedArray.getString(R.styleable.TextSpinner_TS_text);
        nameText = (TextView) findViewById(R.id.ts_name);
        spinner = (MaterialSpinner) findViewById(R.id.ts_spinner);
        nameText.setText(text);
    }

    public void setItems(String...items){
        spinner.setItems(items);
    }

    public String getText(){
        return spinner.getText().toString();
    }

    public int getSelectedIndex(){
        return spinner.getSelectedIndex();
    }
}
