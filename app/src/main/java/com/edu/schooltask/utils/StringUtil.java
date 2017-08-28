package com.edu.schooltask.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import com.edu.schooltask.other.CustomClickableSpan;
import com.edu.schooltask.ui.activity.UserActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 夜夜通宵 on 2017/8/2.
 */

public class StringUtil {

    //判断字符串为空
    public static boolean isEmpty(String string){
        return string == null || string.length() == 0;
    }

    //判断一组字符串为空
    public static int isEmpty(String...strings){
        for(int index = 0; index<strings.length; index++){
            if(strings[index].length() == 0){
                return index;
            }
        }
        return -1;  //全不为空
    }

    //判断字符串在长度范围内
    public static boolean checkLength(String strings,int...lengths){
        if(lengths.length == 1){    //小于某长度
            return strings.length() < lengths[0];
        }
        if(lengths.length == 2){    //长度在范围内
            return strings.length() > lengths[0] && strings.length() < lengths[1];
        }
        return false;
    }

    //判断字符串长度值
    public static boolean equalLength(String string, int length){
        return string.length() == length;
    }

    //获取指定长度的随机字符串
    public static String getRandStr(int length){
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randStr = new StringBuilder();
        for(int i=0; i<length; i++){
            int rand = new Random().nextInt(str.length());
            randStr.append(str.charAt(rand));
        }
        return randStr.toString();
    }

    //判断是否是金钱格式
    public static boolean isMoney(String money){
        Pattern pattern = Pattern.compile("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
        Matcher matcher = pattern.matcher(money);
        return matcher.matches();
    }

    //金钱格式
    public static String moneyFormat(String money){
        int pointIndex = money.lastIndexOf(".");
        StringBuilder sb = new StringBuilder();
        sb.append(money.substring(0,pointIndex));
        sb.reverse();
        for(int i=sb.length()/3; i>0; i--){
            sb.insert(3 * i, ",");
        }
        sb.reverse();
        if(sb.toString().startsWith(","))sb.deleteCharAt(0);
        sb.append(money.substring(pointIndex));
        return sb.toString();
    }

    //@Span
    public static SpannableString atString(final Context context, final String text){
        SpannableString spannableString = new SpannableString(text);
        int start = -1;
        int end = -1;
        for(int i=0; i<text.length(); i++){
            char c = text.charAt(i);
            if(c == '@'){
                start = i;
            }
            else{
                if(start != -1) {
                    if(c == ' '){
                        end = i;
                    } else {
                        if(i == text.length() - 1){
                            end = i + 1;
                        }
                    }
                }
                if(end != -1){
                    final String name = text.substring(start+1, end);
                    CustomClickableSpan span = new CustomClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Intent intent = new Intent(context, UserActivity.class);
                            intent.putExtra("name", name);
                            context.startActivity(intent);
                        }
                    };
                    spannableString.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    start = -1;
                    end = -1;
                }
            }
        }
        return spannableString;
    }
}
