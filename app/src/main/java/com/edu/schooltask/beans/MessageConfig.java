package com.edu.schooltask.beans;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/6/4.
 */

public class MessageConfig implements Serializable{
    boolean vibrate;    //震动
    boolean ring;       //提示应

    public MessageConfig(boolean vibrate, boolean ring) {
        this.vibrate = vibrate;
        this.ring = ring;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isRing() {
        return ring;
    }

    public void setRing(boolean ring) {
        this.ring = ring;
    }
}
