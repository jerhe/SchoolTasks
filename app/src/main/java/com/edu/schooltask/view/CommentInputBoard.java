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
import com.edu.schooltask.utils.KeyBoardUtil;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class CommentInputBoard extends LinearLayout {
    EditText inputText;
    Button btn;
    OnBtnClickListener listener;

    View oppositeView;

    public CommentInputBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_comment_input_board,this);
        inputText = (EditText) findViewById(R.id.cib_input);
        btn = (Button) findViewById(R.id.cib_btn);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.CommentInputBoard);
        String hint = typedArray.getString(R.styleable.CommentInputBoard_inputHint);
        if(hint != null){
            inputText.setHint(hint);
        }
        String btnText = typedArray.getString(R.styleable.CommentInputBoard_btnText);
        if(btnText != null){
            btn.setText(btnText);
        }
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) listener.btnClick(inputText.getText().toString());
            }
        });
    }

    public void setHint(String userName){
        inputText.setHint("回复："+userName);
    }

    public void clearHint(){
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

    public void setOppositeView(View view){
        this.oppositeView = view;
    }

    public void show(){
        if(oppositeView != null) oppositeView.setVisibility(INVISIBLE);
        this.setVisibility(VISIBLE);
        inputText.requestFocus();
        KeyBoardUtil.showKeyBoard(inputText);
    }

    public void show(View oppositeView){
        oppositeView.setVisibility(INVISIBLE);
        show();
    }

    public void hide(){
        if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
        this.setVisibility(View.INVISIBLE);
    }

    public void hide(View oppositeView){
        oppositeView.setVisibility(VISIBLE);
        hide();
    }

    public EditText getInputText(){
        return inputText;
    }

    public interface OnBtnClickListener{
        void btnClick(String comment);
    }


}
