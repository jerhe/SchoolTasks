package com.edu.schooltask.view.Inputtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.utils.AnimationUtil;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class InputTextView extends LinearLayout {
    private InputTextNameView nameText;
    private InputTextTextView inputText;
    private TextView tipText;
    private Boolean inputEnable = true;

    int backgroundColor;
    int backGroundLightColor;
    int fontColor;
    int lineColor;

    public InputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_input_text,this);
        backgroundColor = context.getResources().getColor(R.color.textNameColor);
        backGroundLightColor = context.getResources().getColor(R.color.colorPrimary);
        fontColor = context.getResources().getColor(R.color.fontColor);
        lineColor = context.getResources().getColor(R.color.lineColor);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputTextView);
        String text = typedArray.getString(R.styleable.InputTextView_IT_text);
        String hint = typedArray.getString(R.styleable.InputTextView_IT_hint);
        String inputType = typedArray.getString(R.styleable.InputTextView_IT_inputType);
        String tip = typedArray.getString(R.styleable.InputTextView_IT_tip);
        nameText = (InputTextNameView) findViewById(R.id.it_name);
        inputText = (InputTextTextView)findViewById(R.id.it_input);
        tipText = (TextView) findViewById(R.id.it_tip);
        nameText.setText(text);
        nameText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.requestFocus();
            }
        });
        inputText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && inputEnable){
                    AnimationUtil.colorAnimation(nameText, "background", 100, backgroundColor, backGroundLightColor);
                    AnimationUtil.colorAnimation(inputText, "background", 100, lineColor, backGroundLightColor);
                    nameText.setTextColor(Color.WHITE);
                }
                else{
                    AnimationUtil.colorAnimation(nameText, "background", 0, backGroundLightColor, backgroundColor);
                    AnimationUtil.colorAnimation(inputText, "background", 0, backGroundLightColor, lineColor);
                    nameText.setTextColor(fontColor);
                }
            }
        });
        if(hint != null) inputText.setHint(hint);
        if("password".equals(inputType)){
            inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
        if(tip == null){
            tipText.setVisibility(GONE);
        }
        else{
            tipText.setVisibility(VISIBLE);
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
