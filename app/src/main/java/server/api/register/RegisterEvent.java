package server.api.register;

import com.edu.schooltask.beans.UserBaseInfo;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/23.
 */

public class RegisterEvent extends BaseEvent {
    UserBaseInfo registerUser;
    String token;

    public RegisterEvent() {
        super();
    }

    public RegisterEvent(boolean ok) {
        super(ok);
    }

    public RegisterEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public UserBaseInfo getRegisterUser() {
        return registerUser;
    }

    public void setRegisterUser(UserBaseInfo registerUser) {
        this.registerUser = registerUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
