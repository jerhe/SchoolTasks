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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.utils.DialogUtil;
import com.orhanobut.dialogplus.DialogPlus;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class InputTextView extends LinearLayout {
    private TextView nameText;
    private EditText inputText;
    private PayPasswordView payPasswordView;
    private ImageView tipBtn;
    private Boolean inputEnable = true;

    int backgroundColor;
    int backGroundLightColor;
    int fontColor;
    int lineColor;

    PayPasswordInputBoard payPasswordInputBoard;
    DialogPlus tipDialog;

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
        boolean isNumber = typedArray.getBoolean(R.styleable.InputTextView_IT_number, false);
        typedArray.recycle();

        nameText = (TextView) findViewById(R.id.it_name);
        inputText = (EditText) findViewById(R.id.it_input);
        payPasswordView = (PayPasswordView) findViewById(R.id.it_pay_password);
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
                    nameText.setTextColor(backGroundLightColor);
                }
                else{
                    nameText.setTextColor(fontColor);
                }
            }
        });
        if(hint != null) inputText.setHint(hint);
        if("password".equals(inputType)){
            inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
        if("payPassword".equals(inputType)){
            inputText.setVisibility(GONE);
            payPasswordView.setVisibility(VISIBLE);
            payPasswordView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(payPasswordInputBoard != null) payPasswordInputBoard.show();
                }
            });
        }
        if(tip == null){
            tipBtn.setVisibility(GONE);
        }
        else{
            tipBtn.setVisibility(VISIBLE);
            tipDialog = DialogUtil.createTipDialog(context, text, tip);
            tipBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipDialog.show();
                }
            });
        }
        if(isNumber) inputText.setInputType(InputType.TYPE_CLASS_NUMBER);
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

    /**
     * ---------------------------pay password-------------------------------
     */
    public void setPayPasswordInputBoard(PayPasswordInputBoard inputBoard){
        this.payPasswordInputBoard = inputBoard;
    }

    public void inputPayPassword(int x){
        payPasswordView.input(x);
    }

    public String getPayPassword(){
        return payPasswordView.get();
    }

    public void setPayPasswordFinishedListener(PayPasswordView.PayPasswordFinishedListener listener){
        payPasswordView.setPayPasswordFinishedListener(listener);
    }

    public void deletePayPassword(){
        payPasswordView.delete();
    }

    public void clearPayPassword(){
        payPasswordView.clear();
    }
}
