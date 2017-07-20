package server.api.task.release;

import server.api.base.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ReleaseTaskEvent extends BaseTokenEvent {
    public ReleaseTaskEvent(){}

    public ReleaseTaskEvent(String error){
        this.setCode(1);
        this.setError(error);
    }
}
