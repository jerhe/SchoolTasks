package server.api.base;

import server.api.base.BaseEvent;
import server.api.base.BaseTokenCallBack;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseTokenEvent<T> extends BaseEvent<T> {
    String token;
    boolean validate;
    boolean update;

    public int tokenTag;

    public BaseTokenEvent(){
        tokenTag = BaseTokenCallBack.tokenTag;
    }

    public BaseTokenEvent(boolean ok, int code, String error){
        super(ok, code, error);
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
