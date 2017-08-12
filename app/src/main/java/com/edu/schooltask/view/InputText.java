package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class InputText extends LinearLayout {
    private TextView nameText;
    private EditText inputText;
    private Boolean inputEnable = true;
    private View line;
    public InputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_input_text,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.InputText);
        String text = typedArray.getString(R.styleable.InputText_text);
        String hint = typedArray.getString(R.styleable.InputText_hint);
        String inputType = typedArray.getString(R.styleable.InputText_inputType);
        nameText = (TextView) findViewById(R.id.it_name);
        inputText = (EditText)findViewById(R.id.it_input);
        line = findViewById(R.id.it_underline);
        nameText.setText(text);
        inputText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && inputEnable){
                    line.setBackgroundColor(Color.parseColor("#1b9DFF"));
                }
                else{
                    line.setBackgroundColor(Color.parseColor("#D6D6D6"));
                }
            }
        });
        if(hint != null) inputText.setHint(hint);
        if("password".equals(inputType)){
            inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setText(String text){
        inputText.setText(text);
    }

    public String getText(){
        return inputText.getText().toString();
    }

    public void setInputEnable(boolean inputEnable){
        this.inputEnable = inputEnable;
        if(!inputEnable){
            inputText.setKeyListener(null);
        }
    }

    //清空
    public void clear(){
        inputText.setText("");
        inputText.requestFocus();
    }

    //过滤器
    public void setInputFilter(InputFilter filter){
        inputText.setFilters(new InputFilter[]{filter});
    }

    public EditText getInputText(){
        return inputText;
    }
}
