package server.api.register;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/23.
 */

public class GetCodeEvent extends BaseEvent {
    public GetCodeEvent() {
        super();
    }

    public GetCodeEvent(boolean ok) {
        super(ok);
    }

    public GetCodeEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }
}
