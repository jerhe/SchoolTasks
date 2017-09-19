package server.api;

import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import server.api.result.ResultCallBack;
import server.api.result.Result;

public class Post {
    private String url;
    private Map<String,String> params;
    private ResultCallBack callBack;

    public static Post newPost(){
        return new Post();
    }

    private Post(){}

    public Post url(String url) {
        this.url = url;
        return this;
    }

    public Post addParam(String key, Object value) {
        if(params == null) params = new HashMap<>();
        params.put(key, String.valueOf(value));
        return this;
    }

    public Post result(Result result) {
        callBack = new ResultCallBack(result);
        if(result.isWithToken()){
            if(params == null) params = new HashMap<>();
            UserInfoWithToken user = UserUtil.getLoginUser();
            if(user == null) EventBus.getDefault().post(new UnloginEvent());
            else{
                params.put("userId", user.getUserId());
                params.put("token", user.getToken());
            }
        }
        return this;
    }

    public void post(){
        Calendar c = Calendar.getInstance();
        String time = String.valueOf(c.getTimeInMillis());
        OkHttpUtils.post()
                .url(url)
                .addHeader("time", time)
                .addHeader("sign", EncriptUtil.getSHA(time + "20170828"))
                .params(params)
                .build()
                .execute(callBack);
    }
}
