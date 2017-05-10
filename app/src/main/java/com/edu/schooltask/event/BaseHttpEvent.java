package com.edu.schooltask.event;

import android.support.annotation.Nullable;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class BaseHttpEvent<T> {
    protected boolean ok = false;   //判断是否获取实体数据成功
    protected int code = -1;        //状态码
    protected T t;                  //实体数据

    public BaseHttpEvent(){
        this.ok = false;
    }

    public BaseHttpEvent(boolean ok, int code, T t){
        this.ok = ok;
        this.code = code;
        this.t = t;
    }

    public boolean isOk(){
        return ok;
    }

    public int getCode(){
        return code;
    }

    public T getData(){
        return t;
    }

    public BaseHttpEvent setEvent(int code, T t){
        this.code = code;
        this.t = t;
        return this;
    }
}
