package com.edu.schooltask.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 夜夜通宵 on 2017/8/3.
 */

//手机号过滤器
public class PhoneFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String phoneChars="[0123456789]";
        Pattern pattern = Pattern.compile(phoneChars);
        Matcher matcher = pattern.matcher(source.toString());
        if(!matcher.find()) return "";
        if(source.length() + dest.length() > 11)return "";
        return null;
    }
}
