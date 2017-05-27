package server.api.user;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UploadBGEvent extends BaseTokenEvent {
    public UploadBGEvent() {
        super();
    }

    public UploadBGEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public UploadBGEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
