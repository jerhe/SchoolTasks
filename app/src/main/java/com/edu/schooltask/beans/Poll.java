package com.edu.schooltask.beans;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class Poll implements Serializable {
    long pollId;
    String fromId;
    String toId;
    int type;
    String orderId;
    String content;
    String image;
    int width;
    int height;
    String createTime;

    UserInfoBase fromUser;
    UserInfoBase toUser;

    public Poll(){}

    public long getPollId() {
        return pollId;
    }

    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public UserInfoBase getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserInfoBase fromUser) {
        this.fromUser = fromUser;
    }

    public UserInfoBase getToUser() {
        return toUser;
    }

    public void setToUser(UserInfoBase toUser) {
        this.toUser = toUser;
    }
}
