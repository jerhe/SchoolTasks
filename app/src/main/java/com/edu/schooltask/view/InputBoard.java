package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class InputBoard extends LinearLayout {
    EditText inputText;
    Button btn;
    OnBtnClickListener listener;
    public long parentId;
    public InputBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.input_board,this);
        inputText = (EditText) findViewById(R.id.ib_input);
        btn = (Button) findViewById(R.id.ib_btn);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.InputBoard);
        String hint = typedArray.getString(R.styleable.InputBoard_inputHint);
        if(hint != null){
            inputText.setHint(hint);
        }
        String btnText = typedArray.getString(R.styleable.InputBoard_btnText);
        if(btnText != null){
            btn.setText(btnText);
        }
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) listener.btnClick(parentId, inputText.getText().toString());
            }
        });
    }

    public void setParentId(long parentId, String userName){
        this.parentId = parentId;
        inputText.setHint("回复："+userName);
    }

    public void clearParentId(){
        this.parentId = 0;
        inputText.setHint("请输入评论");
    }

    public void clear(){
        inputText.setText("");
    }

    public void setText(String text){
        inputText.setText(text);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener){
        this.listener = listener;
    }

    public EditText getInputText(){
        return inputText;
    }

    public interface OnBtnClickListener{
        void btnClick(long parentId, String comment);
    }
}
