package server.api.user;

import com.edu.schooltask.beans.UserBaseInfo;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/27.
 */

public class UpdateUserInfoEvent extends BaseTokenEvent {
    UserBaseInfo user;

    public UpdateUserInfoEvent() {
        super();
    }

    public UpdateUserInfoEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public UpdateUserInfoEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }

    public UserBaseInfo getUser() {
        return user;
    }

    public void setUser(UserBaseInfo user) {
        this.user = user;
    }
}
