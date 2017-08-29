package com.edu.schooltask.ui.view.Inputtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class InputTextView extends LinearLayout {
    private InputTextNameView nameText;
    private InputTextTextView inputText;
    private ImageView tipBtn;
    private Boolean inputEnable = true;

    int backgroundColor;
    int backGroundLightColor;
    int fontColor;
    int lineColor;

    PopupWindow tipWindow;

    public InputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_input_text,this);
        backgroundColor = Color.WHITE;
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
        tipBtn = (ImageView) findViewById(R.id.it_tip);
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
                    //nameText.setBackground(backGroundLightColor);
                    //AnimationUtil.colorAnimation(nameText, "background", 100, backgroundColor, backGroundLightColor);
                    //AnimationUtil.colorAnimation(inputText, "background", 100, lineColor, backGroundLightColor);
                    nameText.setTextColor(backGroundLightColor);
                }
                else{
                    //nameText.setBackground(backgroundColor);
                    //AnimationUtil.colorAnimation(nameText, "background", 0, backGroundLightColor, backgroundColor);
                    //AnimationUtil.colorAnimation(inputText, "background", 0, backGroundLightColor, lineColor);
                    nameText.setTextColor(fontColor);
                }
            }
        });
        if(hint != null) inputText.setHint(hint);
        if("password".equals(inputType)){
            inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
        if(tip == null){
            tipBtn.setVisibility(GONE);
        }
        else{
            tipBtn.setVisibility(VISIBLE);
            tipWindow = new PopupWindow(this);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_text_tip, null);
            ((TextView) view.findViewById(R.id.tt_tip)).setText(tip);
            tipWindow.setContentView(view);
            tipWindow.setHeight(LayoutParams.WRAP_CONTENT);
            tipWindow.setOutsideTouchable(true);
            tipBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputText.requestFocus();
                    tipWindow.setWidth(nameText.getWidth() + inputText.getWidth());
                    tipWindow.showAsDropDown(nameText, 0, -5);
                }
            });
        }
    }

    public String getName(){
        return nameText.getText().toString();
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
