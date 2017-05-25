package server.api.register;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/23.
 */

public class CheckCodeEvent extends BaseEvent {
    public CheckCodeEvent() {
        super();
    }

    public CheckCodeEvent(boolean ok) {
        super(ok);
    }

    public CheckCodeEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }
}
