package com.edu.schooltask.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
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

public class InputText extends LinearLayout {
    public final static int BACKGROUND_COLOR = Color.parseColor("#F1F1F1");
    final static int BACKGROUND_LIGHT_COLOR = Color.parseColor("#1B9DFF");
    final static int FONT_COLOR = Color.parseColor("#888888");

    private TextView nameText;
    private EditText inputText;
    private Boolean inputEnable = true;

    GradientDrawable background;

    public InputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_input_text,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.InputText);
        String text = typedArray.getString(R.styleable.InputText_text);
        String hint = typedArray.getString(R.styleable.InputText_hint);
        String inputType = typedArray.getString(R.styleable.InputText_inputType);
        LinearLayout bgLayout = (LinearLayout) findViewById(R.id.it_bg);
        nameText = (TextView) findViewById(R.id.it_name);
        inputText = (EditText)findViewById(R.id.it_input);
        background = (GradientDrawable)bgLayout.getBackground();
        background.setColor(BACKGROUND_COLOR);
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
                    AnimationUtil.colorAnimation(background, "color", 100, BACKGROUND_COLOR, BACKGROUND_LIGHT_COLOR);
                    nameText.setTextColor(Color.WHITE);
                }
                else{
                    AnimationUtil.colorAnimation(background, "color", 0, BACKGROUND_LIGHT_COLOR, BACKGROUND_COLOR);
                    nameText.setTextColor(FONT_COLOR);
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
