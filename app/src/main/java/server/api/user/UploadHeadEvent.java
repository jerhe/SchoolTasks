package server.api.user;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UploadHeadEvent extends BaseTokenEvent {
    public UploadHeadEvent() {
        super();
    }

    public UploadHeadEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public UploadHeadEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
