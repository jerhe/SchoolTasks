package server.api;

import android.util.Log;

import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.api.base.BaseTokenCallBack;
import server.api.user.login.UnloginEvent;

/**
 * Post队列：包含Token的请求需要加入这个队列以保证Token不错乱
 * Created by 夜夜通宵 on 2017/6/3.
 */


public class TokenPost {
    List<TokenRequestBody> bodies = new ArrayList<>();    //RequestBody集合
    boolean posting;    //判断是否正在请求

    public TokenPost(){}

    public TokenRequestBody newPost(){
        return new TokenRequestBody(this);
    }

    public void post(){
        posting = true;
        if(bodies.size() > 0){
            TokenRequestBody tokenRequestBody = bodies.get(0);
            BaseTokenCallBack callBack = new BaseTokenCallBack(tokenRequestBody.getEvent());
            callBack.setResponseListener(new BaseTokenCallBack.ResponseListener() {
                @Override
                public void onResponse() {
                    Log.e("Queue","response");
                    nextPost();
                }
            });

            UserInfoWithToken user = UserUtil.getLoginUser();
            if(user == null) {
                Log.e("Queue","user has lost!");
                EventBus.getDefault().post(new UnloginEvent());
                nextPost();
                return;
            }
            Calendar c = Calendar.getInstance();
            String time = String.valueOf(c.getTimeInMillis());
            Map<String, String> params = tokenRequestBody.getParams();
            if(params == null) params = new HashMap<>();
            params.put("token", user.getToken());
            params.put("userId", user.getUserId());

            OkHttpUtils.post()
                    .url(tokenRequestBody.getUrl())
                    .addHeader("time", time)
                    .addHeader("sign", EncriptUtil.getSHA(time + "20170828"))
                    .params(params)
                    .build()
                    .execute(callBack);
        }
        else{
            posting = false;
        }
    }

    public void nextPost(){
        bodies.remove(0);
        posting = false;
        post();
    }

    public void enqueue(TokenRequestBody body){
        bodies.add(body);
    }
}
