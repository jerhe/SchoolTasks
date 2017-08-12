package com.edu.schooltask.utils;

import com.edu.schooltask.view.InputText;

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
        return string.length() == 0;
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

    //MD5加密
    public static String getMD5(String val) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
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
}
