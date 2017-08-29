package com.edu.schooltask.utils;

import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.beans.FriendList;
import com.edu.schooltask.beans.FriendRequest;
import com.edu.schooltask.beans.MessageConfig;
import com.edu.schooltask.beans.PersonalCenterInfo;
import com.edu.schooltask.beans.comment.TaskCommentList;
import com.edu.schooltask.beans.task.TaskOrderInfo;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.beans.task.TaskOrderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/7/23.
 */

//Event返回类型转换
public class GsonUtil {

    public static List<Detail> toDetailList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<Detail>>() {}.getType());
    }

    public static MessageConfig toUserConfig(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<MessageConfig>() {}.getType());
    }

    public static List<String> toStringList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<String>>() {}.getType());
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

    public static TaskOrderInfo toTaskOrder(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskOrderInfo>() {}.getType());
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

    public static UserInfo toUserInfo(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserInfo>() {}.getType());
    }

    public static UserInfoWithToken toUserInfoWithToken(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<UserInfoWithToken>() {}.getType());
    }

    public static List<TaskOrderInfo> toTaskOrderList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<TaskOrderInfo>>() {}.getType());
    }

    public static List<UserInfo> toUserInfoBaseList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<UserInfo>>() {}.getType());
    }

    public static List<Voucher> toVoucherList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<Voucher>>() {}.getType());
    }

    public static List<TaskOrderItem> toTaskOrderItem(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<TaskOrderItem>>() {}.getType());
    }

    public static TaskItem toTaskItem(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<TaskItem>() {}.getType());
    }

    public static List<FriendRequest> toFriendRequestList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<List<FriendRequest>>() {}.getType());
    }

    public static FriendList toFriendList(Object data){
        return new Gson().fromJson(new Gson().toJson(data), new TypeToken<FriendList>() {}.getType());
    }
}
