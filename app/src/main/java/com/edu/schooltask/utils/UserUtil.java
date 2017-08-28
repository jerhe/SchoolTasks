package com.edu.schooltask.utils;

import android.content.ContentValues;

import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;

import org.litepal.crud.DataSupport;

import static server.api.SchoolTask.HEAD;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class UserUtil {

    //判断是否登录
    public static boolean hasLogin(){
        UserInfoWithToken loginUser = DataSupport.findFirst(UserInfoWithToken.class);
        return loginUser != null;
    }

    //保存登录用户
    public static void saveLoginUser(UserInfoWithToken userInfoWithToken) {
        DataSupport.deleteAll(UserInfoWithToken.class); //清空已存在的用户
        userInfoWithToken.save();
    }

    //删除登录用户
    public static void deleteLoginUser(){
        DataSupport.deleteAll(UserInfoWithToken.class);
    }

    //获取登录用户
    public static UserInfoWithToken getLoginUser(){
        UserInfoWithToken loginUser = DataSupport.findFirst(UserInfoWithToken.class);
        return loginUser;
    }

    //更新Token
    public static void updateToken(String token) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        DataSupport.updateAll(UserInfoWithToken.class, contentValues);
    }

    //更新信息
    public static void updateInfo(UserInfo userInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", userInfo.getName());
        contentValues.put("birth", userInfo.getBirth());
        contentValues.put("email", userInfo.getEmail());
        contentValues.put("school", userInfo.getSchool());
        contentValues.put("sex", userInfo.getSex());
        contentValues.put("sign", userInfo.getSign());
        DataSupport.updateAll(UserInfoWithToken.class, contentValues);
    }

    public static String getHeadUrl(String userId){
        return HEAD + userId + ".png";
    }
}
