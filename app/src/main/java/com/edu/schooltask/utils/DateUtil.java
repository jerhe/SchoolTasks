package com.edu.schooltask.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 夜夜通宵 on 2017/5/12.
 */

public class DateUtil {
    public static Calendar stringToCalendar(String dateStr){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String calendarToString(Calendar c, String f){
        SimpleDateFormat format = new SimpleDateFormat(f);
        Date date = c.getTime();
        return format.format(date);
    }


    public static String getLong(Calendar c1){
        Calendar c2 = Calendar.getInstance();
        long time = (c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000;
        //先判断是否今天
        boolean thisYear = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR) == 0;
        boolean today = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH) == 0;
        if(thisYear){
            if(today){
                if (time >= 0 && time < 60 * 3) { // 3分钟内
                    return "刚刚";
                } else if (time < 3600) {   //一小时内
                    return time / 60 + "分钟前";
                } else {
                    return calendarToString(c1, "HH:mm");
                }
            }
            else{
                return calendarToString(c1, "MM-dd HH:mm");
            }
        }
        else{   //不是今年则显示详细时间
            return calendarToString(c1, "yy-MM-dd HH:mm");
        }
    }

    public static String getLimitTime(String limitTime){
        Calendar now = Calendar.getInstance();
        Calendar limit = stringToCalendar(limitTime);
        if(now.after(limit)) return "任务即将过期";
        long time = limit.getTimeInMillis() - now.getTimeInMillis();
        long hour  = time / (1000 * 60 * 60);
        long minute = time / (1000 * 60) - hour * 60;
        return hour + "小时" + minute + "分钟";
    }

    public static String getDetailTime(String releaseTime){
        Calendar c = stringToCalendar(releaseTime);
        Calendar now = Calendar.getInstance();
        if(c.get(Calendar.YEAR) == now.get(Calendar.YEAR)){
            return calendarToString(c, "MM-dd HH:mm");
        }
        else{
            return calendarToString(c, "yy-MM-dd HH:mm");
        }
    }
}
