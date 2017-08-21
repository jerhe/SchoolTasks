package server.api;

import android.util.Log;

import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.utils.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.api.base.BaseTokenCallBack;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

//Post队列：包含Token的请求需要加入这个队列以保证Token不错乱
public class PostQueue {
    List<RequestBody> requestBodies = new ArrayList<>();    //RequestBody集合
    boolean posting;    //判断是否正在请求

    public PostQueue(){}

    public void newPost(RequestBody requestBody){
        requestBodies.add(requestBody);
        if(!posting) post();
    }

    private void post(){
        posting = true;
        if(requestBodies.size() > 0){
            RequestBody requestBody = requestBodies.get(0);
            BaseTokenCallBack callBack = requestBody.getCallBack();
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
            UserInfoWithToken user = UserUtil.getLoginUser();
            if(user == null) {
                throw new RuntimeException("User has lost!");
            }
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
