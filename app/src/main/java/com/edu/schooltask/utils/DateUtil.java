package com.edu.schooltask.utils;

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
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String calendarToString(Calendar c, String f){
        SimpleDateFormat format = new SimpleDateFormat(f); //不显示秒
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
                if (time > 0 && time < 60 * 3) { // 3分钟内
                    return "刚刚";
                } else if (time < 3600) {   //一小时内
                    return time / 60 + "分钟前";
                } else {
                    return calendarToString(c1, "HH:mm");
                }
            }
            else{
                return calendarToString(c1, "M-d HH:mm");
            }
        }
        else{   //不是今年则显示详细时间
            return calendarToString(c1, "y-M-d HH:mm");
        }
    }

    public static String getMessageLong(String time){
        Calendar c1 = stringToCalendar(time);
        Calendar c2 = Calendar.getInstance();
        boolean thisYear = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR) == 0;
        boolean today = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH) == 0;
        boolean yestoday = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH) == 1;
        boolean frontday = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH) == 2;
        if(thisYear){
            if(today){
                return calendarToString(c1, "HH:mm");
            }
            else{
                if(yestoday){
                    return calendarToString(c1, "昨天 HH:mm");
                }
                else{
                    if(frontday){
                        return calendarToString(c1, "前天 HH:mm");
                    }
                    else{
                        return calendarToString(c1, "M月d日 HH:mm");
                    }
                }
            }
        }
        else{
            return calendarToString(c1, "yyyy年M月d日 HH:mm");
        }
    }
}
