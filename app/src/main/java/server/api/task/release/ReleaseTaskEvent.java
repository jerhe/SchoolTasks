package server.api.task.release;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ReleaseTaskEvent extends BaseTokenEvent {
    public ReleaseTaskEvent() {
    }

    public ReleaseTaskEvent(String error){
        this.setError(error);
    }

    public ReleaseTaskEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public ReleaseTaskEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
