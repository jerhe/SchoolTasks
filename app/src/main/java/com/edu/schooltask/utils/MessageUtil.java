package com.edu.schooltask.utils;

import android.content.ContentValues;

import com.edu.schooltask.beans.MessageItem;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.PrivateMessage;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/12.
 */

public class MessageUtil {
    public static boolean isPrivateMessage; //是否正在私信Activity
    public static String userId;

    public static void open(String userId){
        isPrivateMessage = true;
        MessageUtil.userId = userId;
    }

    public static void close(){
        isPrivateMessage = false;
        MessageUtil.userId = null;
    }
}
