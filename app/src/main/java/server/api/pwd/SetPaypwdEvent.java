package server.api.pwd;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/23.
 */

public class SetPaypwdEvent extends BaseTokenEvent {
    public SetPaypwdEvent() {
        super();
    }

    public SetPaypwdEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public SetPaypwdEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
