package server.api;

import android.util.Log;

import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.api.base.BaseTokenCallBack;

/**
 * Post队列：包含Token的请求需要加入这个队列以保证Token不错乱
 * Created by 夜夜通宵 on 2017/6/3.
 */


public class TokenPost {
    static List<TokenRequestBody> bodies = new ArrayList<>();    //RequestBody集合
    static boolean posting;    //判断是否正在请求

    public static TokenRequestBody newPost(){
        return new TokenRequestBody();
    }

    //按队列顺序post
    public static void post(){
        posting = true;
        if(bodies.size() > 0){
            TokenRequestBody tokenRequestBody = bodies.get(0);
            BaseTokenCallBack callBack = tokenRequestBody.getCallBack();
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
                bodies.clear();
                posting = false;
                return;
            }
            packParams(user, tokenRequestBody).execute(callBack);
        }
        else{
            posting = false;
        }
    }

    //并行post
    public static void post(TokenRequestBody requestBody){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user == null) {
            Log.e("Queue","user has lost");
            EventBus.getDefault().post(new UnloginEvent());
            return;
        }
        packParams(user, requestBody).execute(requestBody.getCallBack());
    }

    private static void nextPost(){
        bodies.remove(0);
        posting = false;
        post();
    }

    public static void enqueue(TokenRequestBody body){
        if(body.getCallBack() == null) throw new RuntimeException("callback must not be null!");
        bodies.add(body);
    }

    private static RequestCall packParams(UserInfoWithToken user, TokenRequestBody tokenRequestBody){
        Calendar c = Calendar.getInstance();
        String time = String.valueOf(c.getTimeInMillis());
        Map<String, String> params = tokenRequestBody.getParams();
        if(params == null) params = new HashMap<>();
        params.put("userId", user.getUserId());
        params.put("token", user.getToken());
        return OkHttpUtils.post()
                    .url(tokenRequestBody.getUrl())
                    .addHeader("time", time)
                    .addHeader("sign", EncriptUtil.getSHA(time + "20170828"))
                    .params(params)
                    .build();
    }
}
