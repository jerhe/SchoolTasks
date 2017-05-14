package com.edu.schooltask.event;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class BaseHttpEvent<T> {
    protected boolean ok = false;   //判断是否获取实体数据成功
    protected int code = -1;        //状态码
    protected String error = "";         //错误消息
    protected JSONObject data;                  //返回数据
    protected T bean;


    public BaseHttpEvent(){
        this.ok = false;
    }

    public BaseHttpEvent(boolean ok){   //checkToken 网络连接失败
        this.ok = ok;
        this.code = -1;
        this.error = "连接失败,请检查网络";
    }


    public BaseHttpEvent(boolean ok, int code, JSONObject data){
        this.ok = ok;
        this.code = code;
        this.data = data;
    }

    public boolean isOk(){
        return ok;
    }

    public int getCode(){
        return code;
    }

    public String getError(){
        return error;
    }

    public JSONObject getData(){
        return data;
    }

    public BaseHttpEvent setEvent(int code, JSONObject data){
        if(code == 0) {
            ok = true;
            data.remove("result");
        }
        else{
            if(data != null){
                try {
                    error = data.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                switch (code){
                    case -1:
                        error = "连接失败,请检查网络";
                        break;
                    case -2:
                        error = "数据异常";
                        break;
                }
            }

        }
        this.code = code;
        this.data = data;
        return this;
    }
}
