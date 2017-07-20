package com.edu.schooltask.data;

import android.content.Context;

import com.edu.schooltask.beans.User;
import com.edu.schooltask.utils.ACache;
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

    public void removeDate(String key) {
        mLruCache.remove(key);
        mDiskCache.remove(key);
    }


    public void saveUser(User user) {
        saveData("user", user);
    }

    public User getUser() {
        return getData("user");
    }

    public void removeUser() {
        removeDate("user");
    }

    public void refreshToken(String token){
        User user = getUser();
        user.setToken(token);
        saveUser(user);
    }

    public void saveSchool(List<String> schools){
        saveListData("school", schools);
    }

    public ArrayList<String> getSchool(){
        return getData("school");
    }
}
