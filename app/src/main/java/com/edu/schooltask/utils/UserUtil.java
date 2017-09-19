package com.edu.schooltask.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;

import org.litepal.crud.DataSupport;

import static server.api.SchoolTask.BG;
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
        contentValues.put("head", userInfo.getHead());
        contentValues.put("bg", userInfo.getBg());
        contentValues.put("birth", userInfo.getBirth());
        contentValues.put("email", userInfo.getEmail());
        contentValues.put("school", userInfo.getSchool());
        contentValues.put("sex", userInfo.getSex());
        contentValues.put("sign", userInfo.getSign());
        DataSupport.updateAll(UserInfoWithToken.class, contentValues);
    }

    //设置头像
    public static void setHead(Context context, String head, ImageView imageView){
        if(StringUtil.isEmpty(head)){    //头像为空
            Glide.with(context) //设置默认头像
                    .load(R.drawable.ic_default_head)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_default_head)
                    .into(imageView);
            return;
        }
        Glide.with(context)
                .load(head)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_head)
                .error(R.drawable.ic_default_head)
                .into(imageView);
    }

    public static void setHead(Context context, int resId, ImageView imageView){
        Glide.with(context) //设置默认头像
                .load(resId)
                .dontAnimate()
                .into(imageView);
    }

    //设置背景
    public static void setBackground(Context context, String bg, ImageView imageView){
        if(StringUtil.isEmpty(bg)){    //背景为空
            Glide.with(context) //设置默认背景
                    .load(R.drawable.background)
                    .into(imageView);
            return;
        }
        Glide.with(context)
                .load(bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.background)
                .error(R.drawable.background)
                .into(imageView);
    }

    //根据本地用户信息转换成融云用户信息
    public static io.rong.imlib.model.UserInfo toRongUserInfo(UserInfo userInfo){
        String head = userInfo.getHead();
        Uri uri;
        if(StringUtil.isEmpty(head)) uri = Uri.parse("");
        else uri = Uri.parse(head);
        return new io.rong.imlib.model.UserInfo(userInfo.getUserId(), userInfo.getName(), uri);
    }
}
