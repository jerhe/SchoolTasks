package server.api.task.count;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class GetTaskCountEvent extends BaseEvent {
    int commentCount;
    int lookCount;

    public GetTaskCountEvent() {
    }

    public GetTaskCountEvent(boolean ok) {
        super(ok);
    }

    public GetTaskCountEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLookCount() {
        return lookCount;
    }

    public void setLookCount(int lookCount) {
        this.lookCount = lookCount;
    }
}
