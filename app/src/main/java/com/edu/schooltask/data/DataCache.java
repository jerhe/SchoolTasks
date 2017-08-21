package com.edu.schooltask.data;

import android.content.Context;

import com.edu.schooltask.beans.MessageItem;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.PrivateMessage;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.other.ACache;
import com.edu.schooltask.utils.FileUtil;

import java.io.File;

import android.support.annotation.NonNull;
import android.util.LruCache;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据缓存工具
 */
public class DataCache {
    private static int M = 1024 * 1024;
    ACache mDiskCache;  //磁盘缓存
    LruCache<String, Object> mLruCache;     //内存缓存

    public DataCache(Context context) {
        mDiskCache = ACache.get(new File(FileUtil.getExternalCacheDir(context.getApplicationContext(), "diy-data")));
        mLruCache = new LruCache<>(5 * M);
    }

    public <T extends Serializable> void saveListData(String key, List<T> data) {
        ArrayList<T> datas = (ArrayList<T>) data;
        mLruCache.put(key, datas);
        mDiskCache.put(key, datas, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
    }

    public <T extends Serializable> void saveData(@NonNull String key, @NonNull T data) {
        mLruCache.put(key, data);
        mDiskCache.put(key, data, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
    }

    public <T extends Serializable> T getData(@NonNull String key) {
        T result = (T) mLruCache.get(key);
        if (result == null) {
            result = (T) mDiskCache.getAsObject(key);
            if (result != null) {
                mLruCache.put(key, result);
            }
        }
        return result;
    }

    public Integer getInt(String key, int defaultValue){
        if(getData(key) == null) return defaultValue;
        else return getData(key);
    }

    public void removeDate(String key) {
        mLruCache.remove(key);
        mDiskCache.remove(key);
    }

    //保存粉丝
    public void saveFans(String userId, List<UserInfo> fans){
        saveListData("fans" + userId, fans);
    }

    //读取粉丝
    public List<UserInfo> getFans(String userId){
        List<UserInfo> fans = getData("fans" + userId);
        if(fans == null) fans = new ArrayList<>();
        return fans;
    }

    //保存关注
    public void saveFollowers(String userId, List<UserInfo> fans){
        saveListData("followers" + userId, fans);
    }

    //读取关注
    public List<UserInfo> getFollowers(String userId){
        List<UserInfo> followers = getData("followers" + userId);
        if(followers == null) followers = new ArrayList<>();
        return followers;
    }

    //获取所有私信
    public List<PrivateMessage> getPrivateMessage(String userId){
        List<PrivateMessage> list = getData(userId + "privateMessage");
        if(list == null) return new ArrayList<>();
        return list;
    }

    //获取与某用户的私信消息
    public List<PrivateMessage> getPrivateMessage(String userId, String toUserId){
        List<PrivateMessage> privateMessages = getPrivateMessage(userId);
        List<PrivateMessage> list = new ArrayList<>();
        for(PrivateMessage privateMessage : privateMessages){
            Poll poll = privateMessage.getPoll();
            if(poll.getFromId().equals(toUserId) || poll.getToId().equals(toUserId)) list.add(privateMessage);
        }
        return list;
    }

    //存储私信消息
    public void savePrivateMessage(String userId, PrivateMessage privateMessage){
        List<PrivateMessage> privateMessages = getPrivateMessage(userId);
        privateMessages.add(privateMessage);
        saveListData(userId + "privateMessage", privateMessages);
    }

    //获取所有未读消息
    public List<PrivateMessage> getNotReadMessage(String userId){
        List<PrivateMessage> privateMessages = getPrivateMessage(userId);
        List<PrivateMessage> notReadMessage = new ArrayList<>();
        for (PrivateMessage privateMessage : privateMessages){
            if(!privateMessage.isHasRead()) notReadMessage.add(privateMessage);
        }
        return notReadMessage;
    }

    //设置与某用户的私信已读
    public void setHasRead(String userId, String toUserId){
        List<PrivateMessage> privateMessages = getPrivateMessage(userId);
        for(PrivateMessage privateMessage : privateMessages){
            if(privateMessage.getPoll().getFromId().equals(toUserId)) privateMessage.setHasRead(true);
        }
        saveListData(userId + "privateMessage", privateMessages);
    }

    //获取消息列表
    public List<MessageItem> getMessageItem(String userId){
        List<MessageItem> list = getData(userId + "messageItem");
        if(list == null) return new ArrayList<>();
        return list;
    }

    //存储消息列表
    public void saveMessageItem(String userId, List<MessageItem> list){
        saveListData(userId + "messageItem", list);
    }

    public void saveSchool(List<String> schools){
        saveListData("school", schools);
    }

    public ArrayList<String> getSchool(){
        return getData("school");
    }
}
