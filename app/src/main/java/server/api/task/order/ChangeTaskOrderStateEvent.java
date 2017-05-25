package server.api.task.order;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/24.
 */

public class ChangeTaskOrderStateEvent extends BaseTokenEvent {
    public ChangeTaskOrderStateEvent() {
        super();
    }

    public ChangeTaskOrderStateEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public ChangeTaskOrderStateEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
