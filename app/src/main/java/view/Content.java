package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import utils.TextUtil;

/**
 * Created by 夜夜通宵 on 2017/5/7.
 */

public class Content extends RelativeLayout{
    private EditText inputText;
    private TextView numText;
    public Content(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_content,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.Content);
        final int maxLength = typedArray.getInt(R.styleable.Content_maxLength, Integer.MAX_VALUE);
        String hint = typedArray.getString(R.styleable.Content_contentHint);

        inputText = (EditText) findViewById(R.id.content_input);
        numText = (TextView) findViewById(R.id.content_num);

        TextUtil.setLengthFilter(inputText, maxLength);   //限制输入长度
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

    public String getText(){
        return inputText.getText().toString();
    }
}
