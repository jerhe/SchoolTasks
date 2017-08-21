package com.edu.schooltask.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.view.Inputtextview.InputTextNameView;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class SelectText extends RelativeLayout {
    InputTextNameView nameText;
    MaterialSpinner spinner;

    public SelectText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_select_text,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SelectText);
        String text = typedArray.getString(R.styleable.SelectText_ST_text);
        nameText = (InputTextNameView) findViewById(R.id.st_name);
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
