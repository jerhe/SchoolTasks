package com.edu.schooltask.utils;

import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.beans.PersonalCenterInfo;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.TaskCommentList;
import com.edu.schooltask.beans.TaskCount;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.item.TaskItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/7/23.
 */

//Event返回类型转换
public class GsonUtil {

    public static List<Detail> toDetailList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<Detail>>() {}.getType());
    }

    public static UserConfig toUserConfig(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserConfig>() {}.getType());
    }

    public static List<String> toStringList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<String>>() {}.getType());
    }

    public static List<Poll> toPollList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<Poll>>() {}.getType());
    }

    public static Poll toPoll(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<Poll>() {}.getType());
    }

    public static PersonalCenterInfo toPersonalCenterInfo(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<PersonalCenterInfo>() {}.getType());
    }

    public static UploadKey toUploadKey(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UploadKey>() {}.getType());
    }

    public static TaskUploadKey toTaskUploadKey(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskUploadKey>() {}.getType());
    }

    public static List<TaskItem> toTaskItemList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<TaskItem>>() {}.getType());
    }

    public static TaskOrder toTaskOrder(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskOrder>() {}.getType());
    }

    public static TaskCommentList toTaskCommentList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskCommentList>() {}.getType());
    }

    public static UserHomePageInfo toUserHomePageInfo(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserHomePageInfo>() {}.getType());
    }

    public static Integer toInteger(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<Integer>() {}.getType());
    }

    public static UserInfoBase toUserInfoBase(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserInfoBase>() {}.getType());
    }

    public static TaskCount toTaskCount(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskCount>() {}.getType());
    }

    public static UserInfo toUserInfo(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserInfo>() {}.getType());
    }

    public static List<TaskOrder> toTaskOrderList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<TaskOrder>>() {}.getType());
    }

    public static List<UserInfoBase> toUserInfoBaseList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<UserInfoBase>>() {}.getType());
    }
}
