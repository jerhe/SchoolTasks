package server.api.event;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UploadBGEvent {
    boolean ok;
    String url;

    public UploadBGEvent(boolean ok, String url) {
        this.ok = ok;
        this.url = url;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
