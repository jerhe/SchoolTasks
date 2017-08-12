package com.edu.schooltask.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 夜夜通宵 on 2017/8/3.
 */

//密码过滤器
public class PasswordFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String reg="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\]<>/?~！ @#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source.toString());
        if(matcher.find()) return "";
        if(source.length() + dest.length() > 16)return "";
        return null;
    }
}
