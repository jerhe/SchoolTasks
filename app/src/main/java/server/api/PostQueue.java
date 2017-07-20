package server.api;

import android.util.Log;

import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.api.base.BaseTokenCallBack;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class PostQueue {
    List<RequestBody> requestBodies = new ArrayList<>();
    boolean posting;

    DataCache mDataCache;

    public PostQueue(DataCache dataCache){
        mDataCache = dataCache;
    }

    public void newPost(RequestBody requestBody){
        requestBodies.add(requestBody);
        if(!posting) post();
    }

    private void post(){
        posting = true;
        if(requestBodies.size() > 0){
            RequestBody requestBody = requestBodies.get(0);
            BaseTokenCallBack callBack = requestBody.getCallBack();
            Log.e("Queue","post" + " @class:" + callBack.getEventClass());
            callBack.setResponseListener(new BaseTokenCallBack.ResponseListener() {
                @Override
                public void onResponse() {
                    Log.e("Queue","response");
                    requestBodies.remove(0);
                    posting = false;
                    post();
                }
            });

            PostFormBuilder builder = OkHttpUtils.post();
            builder.url(requestBody.getUrl());
            User user = mDataCache.getUser();
            builder.addHeader("token", user.getToken());
            Map<String, String> params = requestBody.getParams();
            if(params != null) builder.params(params);
            builder.build().execute(callBack);
        }
        else{
            posting = false;
        }
    }
}
