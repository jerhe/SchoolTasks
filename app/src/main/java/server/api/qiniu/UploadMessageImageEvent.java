package server.api.qiniu;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class UploadMessageImageEvent {
    boolean ok;
    String image;
    int imageWidth;
    int imageHeight;

    public UploadMessageImageEvent(boolean ok, String image, int imageWidth, int imageHeight) {
        this.ok = ok;
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
