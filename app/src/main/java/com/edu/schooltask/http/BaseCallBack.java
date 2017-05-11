package com.edu.schooltask.http;

import android.util.Log;

import com.edu.schooltask.event.BaseHttpEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;



/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class BaseCallBack implements okhttp3.Callback {
    protected BaseHttpEvent event;

    public BaseCallBack(){}
    public BaseCallBack(BaseHttpEvent event){
        this.event = event;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(response.isSuccessful()){    //1.成功
            try {
                JSONObject responseJSONObject = new JSONObject(response.body().string());
                EventBus.getDefault().post(event.setEvent(responseJSONObject.getInt("result"),
                        responseJSONObject));
            } catch (JSONException e) {
                EventBus.getDefault().post(event.setEvent(-2, null));
                e.printStackTrace();
            }
        }
        else{   //2.失败
            EventBus.getDefault().post(event.setEvent(-1, null));
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {   //3.失败
        EventBus.getDefault().post(event.setEvent(-1, null));
    }
}
