package com.edu.schooltask.beans;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class Poll implements Serializable {
    long pollId;
    UserBaseInfo fromUser;
    UserBaseInfo toUser;
    int type;
    String orderId;
    String msg;
    String image;
    boolean hasRead;
    String time;

    int width;
    int height;

    public Poll(){}

    public long getPollId() {
        return pollId;
    }

    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserBaseInfo getToUser() {
        return toUser;
    }

    public void setToUser(UserBaseInfo toUser) {
        this.toUser = toUser;
    }

    public UserBaseInfo getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserBaseInfo fromUser) {
        this.fromUser = fromUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
