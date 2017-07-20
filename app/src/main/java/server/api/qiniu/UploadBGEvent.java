package server.api.qiniu;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UploadBGEvent {
    boolean ok;
    public UploadBGEvent(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
