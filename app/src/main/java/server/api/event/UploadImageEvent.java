package server.api.event;

/**
 * Created by 夜夜通宵 on 2017/5/30.
 */

public class UploadImageEvent {
    private boolean ok;
    private int index;

    public UploadImageEvent(boolean ok, int index){
        this.ok = ok;
        this.index = index;
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
