package server.api.task.accept;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/24.
 */

public class AcceptTaskEvent extends BaseTokenEvent {
    public AcceptTaskEvent() {
        super();
    }

    public AcceptTaskEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public AcceptTaskEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
