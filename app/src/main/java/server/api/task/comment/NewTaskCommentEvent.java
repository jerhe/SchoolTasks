package server.api.task.comment;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/20.
 */

public class NewTaskCommentEvent extends BaseTokenEvent {
    public NewTaskCommentEvent() {
        super();
    }

    public NewTaskCommentEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public NewTaskCommentEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }
}
