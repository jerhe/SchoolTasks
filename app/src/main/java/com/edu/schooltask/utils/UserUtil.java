package com.edu.schooltask.utils;

import android.content.ContentValues;

import com.edu.schooltask.beans.LoginUser;
import com.edu.schooltask.beans.UserInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class UserUtil {
    //判断是否登录
    public static boolean hasLogin(){
        LoginUser loginUser = DataSupport.findFirst(LoginUser.class);
        return loginUser != null;
    }

    //保存登录用户
    public static void saveLoginUser(UserInfo userInfo) {
        DataSupport.deleteAll(LoginUser.class); //清空已存在的用户
        LoginUser loginUser = new LoginUser(userInfo);
        loginUser.save();
    }

    //删除登录用户
    public static void deleteLoginUser(){
        DataSupport.deleteAll(LoginUser.class);
    }

    //获取登录用户
    public static UserInfo getLoginUser(){
        LoginUser loginUser = DataSupport.findFirst(LoginUser.class);
        return loginUser;
    }

    //更新Token
    public static void updateToken(String token) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        DataSupport.updateAll(LoginUser.class, contentValues);
    }
}
