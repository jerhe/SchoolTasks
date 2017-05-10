package com.edu.schooltask.http;

import com.edu.schooltask.event.BaseHttpEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;



/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class HttpCallBack implements okhttp3.Callback {
    protected BaseHttpEvent event;
    public HttpResponse httpResponse;

    public HttpCallBack(){}
    public HttpCallBack(BaseHttpEvent event){
        this.event = event;
    }
    public HttpCallBack(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        httpResponse.code = -1;
        try {
            httpResponse.handler();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String str = response.body().string();
            if(str.length() != 0){
                JSONObject jsonObject = new JSONObject(str);
                httpResponse.code = jsonObject.getInt("result");
                httpResponse.data = jsonObject;   //数据返回成功
                httpResponse.handler();

                EventBus.getDefault().post(event.setEvent(jsonObject.getInt("result"),
                        httpResponse.data));
            }

        } catch (Exception e) {
            httpResponse.code = -2;
            e.printStackTrace();
        }
    }
}
