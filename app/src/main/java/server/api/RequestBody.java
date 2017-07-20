package server.api;

import java.util.Map;

import server.api.base.BaseTokenCallBack;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class RequestBody {
    String url;
    Map<String,String> params;
    BaseTokenCallBack callBack;

    public RequestBody(){}

    public RequestBody(String url, Map<String, String> params, BaseTokenCallBack callBack) {
        this.url = url;
        this.params = params;
        this.callBack = callBack;
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

    public BaseTokenCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(BaseTokenCallBack callBack) {
        this.callBack = callBack;
    }
}
