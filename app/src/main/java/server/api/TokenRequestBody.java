package server.api;

import java.util.HashMap;
import java.util.Map;

import server.api.base.BaseTokenCallBack;
import server.api.base.BaseTokenEvent;

/**
 * 附加Token请求体
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class TokenRequestBody {
    private String url;
    private Map<String,String> params;
    private BaseTokenCallBack callBack;

    public TokenRequestBody(){}

    public BaseTokenCallBack getCallBack(){
        return callBack;
    }

    public Map getParams(){
        return params;
    }

    public String getUrl(){
        return url;
    }

    public TokenRequestBody url(String url) {
        this.url = url;
        return this;
    }

    public TokenRequestBody addParam(String key, Object value) {
        if(this.params == null) this.params = new HashMap<>();
        params.put(key, String.valueOf(value));
        return this;
    }

    public TokenRequestBody event(BaseTokenEvent event) {
        callBack = new BaseTokenCallBack(event);
        return this;
    }

    //请求
    public void post(){
        TokenPost.post(this);
    }

    //入队
    public void enqueue(){
        TokenPost.enqueue(this);
        if(!TokenPost.posting) TokenPost.post();
    }

}
