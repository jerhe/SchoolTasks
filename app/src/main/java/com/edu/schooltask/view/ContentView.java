package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/7.
 */

public class ContentView extends RelativeLayout{
    private EditText inputText;
    private TextView numText;
    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_content,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ContentView);
        final int maxLength = typedArray.getInt(R.styleable.ContentView_maxLength, Integer.MAX_VALUE);
        String hint = typedArray.getString(R.styleable.ContentView_contentHint);
        typedArray.recycle();

        inputText = (EditText) findViewById(R.id.content_input);
        numText = (TextView) findViewById(R.id.content_num);

        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                numText.setText((maxLength - s.length()+""));
            }
        });
        numText.setText((maxLength +""));

        if(hint != null){
            inputText.setHint(hint);
        }
    }

    public void setText(String text){
        inputText.setText(text);
    }

    public String getText(){
        return inputText.getText().toString();
    }

    public void setHint(String text){
        inputText.setHint(text);
    }
}
