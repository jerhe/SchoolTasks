package com.edu.schooltask.item;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ImageItem implements Serializable{
    int type;
    String path;

    public ImageItem(int type){
        this.type = type;
    }

    public ImageItem(int type, String path) {
        this.type = type;
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
