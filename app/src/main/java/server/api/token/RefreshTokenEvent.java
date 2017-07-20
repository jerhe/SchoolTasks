package server.api.token;

import server.api.base.BaseCallBack;
import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class RefreshTokenEvent{
    String token;

    public RefreshTokenEvent(){

    }

    public RefreshTokenEvent(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
