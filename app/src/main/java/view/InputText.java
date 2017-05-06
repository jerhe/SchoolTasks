package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import utils.TextUtil;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class InputText extends LinearLayout {
    private TextView nameText;
    private EditText inputText;
    private View line;
    public InputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_input_text,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.InputText);
        String text = typedArray.getString(R.styleable.InputText_text);
        String inputType = typedArray.getString(R.styleable.InputText_inputType);
        nameText = (TextView) findViewById(R.id.it_name);
        inputText = (EditText)findViewById(R.id.it_input);
        line = findViewById(R.id.it_underline);
        nameText.setText(text);
        inputText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    line.setBackgroundColor(Color.parseColor("#1b9DFF"));
                }
                else{
                    line.setBackgroundColor(Color.parseColor("#D6D6D6"));
                }
            }
        });
        if("password".equals(inputType)){
            inputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
    }

    public String getText(){
        return inputText.getText().toString();
    }


    /**
     * 设置过滤
     * @param type 类型 0:手机 1...
     */
    public void setInputFilter(int type){
        switch (type){
            case 0:
                TextUtil.setPhoneFilter(inputText);
                break;
            case 1:
                TextUtil.setSchoolFilter(inputText);
                break;
            case 2:
                TextUtil.setNameFilter(inputText);
                break;
            case 3:
                TextUtil.setPwdFilter(inputText);
                break;
        }

    }
}
