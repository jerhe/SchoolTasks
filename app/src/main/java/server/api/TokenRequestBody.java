package server.api;

import java.util.HashMap;
import java.util.Map;

import server.api.base.BaseTokenEvent;

/**
 * 附加Token请求体
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class TokenRequestBody {
    private String url;
    private Map<String,String> params;
    private BaseTokenEvent event;
    private TokenPost tokenPost;

    public TokenRequestBody(TokenPost tokenPost){
        this.tokenPost = tokenPost;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public BaseTokenEvent getEvent() {
        return event;
    }

    public void setEvent(BaseTokenEvent event) {
        this.event = event;
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
        this.event = event;
        return this;
    }

    public void enqueue(){
        tokenPost.enqueue(this);
        if(!tokenPost.posting) tokenPost.post();
    }

}
