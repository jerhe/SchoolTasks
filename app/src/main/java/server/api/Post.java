package server.api;

import com.edu.schooltask.utils.EncriptUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import server.api.base.BaseCallBack;
import server.api.base.BaseEvent;

/**
 * 无Token请求体
 * Created by 夜夜通宵 on 2017/8/28.
 */

public class Post {
    private String url;
    private Map<String,String> params;
    private BaseCallBack callBack;

    public static Post newPost(){
        return new Post();
    }

    private Post(){}

    public Post url(String url) {
        this.url = url;
        return this;
    }

    public Post addParam(String key, Object value) {
        if(this.params == null) this.params = new HashMap<>();
        this.params.put(key, String.valueOf(value));
        return this;
    }

    public Post event(BaseEvent event) {
        callBack = new BaseCallBack(event);
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
