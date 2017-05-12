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

    public static String dateToString(Calendar c){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = c.getTime();
        return format.format(date);
    }

    public static String getLong(Calendar c1){
        Calendar c2 = Calendar.getInstance();
        long time = (c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000;

        StringBuffer sb = new StringBuffer();
        if (time > 0 && time < 60 * 3) { // 3分钟内
            return "刚刚";
        } else if (time < 3600) {   //一小时内
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 +"小时前";
        }else if (time >= 3600 * 24 && time < 3600 * 48) {
            return "昨天";
        }else if (time >= 3600 * 48 && time < 3600 * 72) {
            return "前天";
        }else if (time >= 3600 * 72 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        }else if (time >= 3600 * 24 *30 && time < 3600 * 24 * 30 * 365) {
            return time / 3600 / 24 / 30 + "个月前";
        }else return time / 3600 / 24 / 30 / 365 + "年前";
    }
}
