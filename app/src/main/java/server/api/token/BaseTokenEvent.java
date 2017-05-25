package server.api.token;

import com.edu.schooltask.data.DataCache;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseTokenEvent extends BaseEvent {
    boolean validate;
    boolean update;
    String token;

    public BaseTokenEvent(){}

    public BaseTokenEvent(boolean ok, int code, String error){
        super(ok, code, error);
    }

    public BaseTokenEvent(boolean validate, boolean update, String token){
        this.validate = validate;
        this.update = update;
        this.token = token;
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
