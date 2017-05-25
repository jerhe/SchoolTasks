package com.edu.schooltask.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 夜夜通宵 on 2017/5/12.
 */

public class DateUtil {
    public static Calendar stringToDate(String dateStr){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String calendarToString(Calendar c){
        SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm"); //不显示秒
        Date date = c.getTime();
        return format.format(date);
    }

    public static String calendarToStringNoYear(Calendar c){
        SimpleDateFormat format = new SimpleDateFormat("M-d HH:mm"); //不显示秒
        Date date = c.getTime();
        return format.format(date);
    }

    public static String getLong(Calendar c1){
        Calendar c2 = Calendar.getInstance();
        long time = (c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000;
        //先判断是否今天
        boolean thisYear = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR) == 0;
        if(thisYear){
            if (time > 0 && time < 60 * 3) { // 3分钟内
                return "刚刚";
            } else if (time < 3600) {   //一小时内
                return time / 60 + "分钟前";
            } else {    //显示具体时间（不包含年份）
                return calendarToStringNoYear(c1);
            }
        }
        else{   //不是今天则显示详细时间
            return calendarToString(c1);
        }


    }
}
