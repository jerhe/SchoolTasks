package com.edu.schooltask.http;

import org.json.JSONObject;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public abstract class HttpResponse {
    public int code;   //返回码    -2:数据异常 -1:发送失败 0:成功 其余看具体情况
    public JSONObject data;    //返回数据

    public abstract void handler() throws Exception;
}
