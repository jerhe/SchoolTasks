package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/27.
 */

public class TextItem extends LinearLayout {
    TextView nameText;
    TextView textText;
    public TextItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_text_item,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextItem);
        String name = typedArray.getString(R.styleable.TextItem_itemName);
        typedArray.recycle();

        nameText = (TextView) findViewById(R.id.ti_name);
        textText = (TextView) findViewById(R.id.ti_text);

        nameText.setText(name);
    }

    public void setText(String text){
        textText.setText(text);
    }

    public String getText(){
        return textText.getText().toString();
    }

    public void setName(String name){
        nameText.setText(name);
    }

    public String getName(){
        return nameText.getText().toString();
    }
}
