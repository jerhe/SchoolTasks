package com.edu.schooltask.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by 夜夜通宵 on 2017/8/3.
 */

//昵称过滤器
public class NameFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if(source.length() + dest.length() > 8) return "";
        return null;
    }
}
