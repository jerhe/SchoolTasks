package server.api.event.qiniu;

/**
 * Created by 夜夜通宵 on 2017/5/30.
 */

public class UploadTaskImageEvent {
    int index;  //图片序号
    boolean ok;
    public UploadTaskImageEvent(int index, boolean ok){
        this.index = index;
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
