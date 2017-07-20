package com.edu.schooltask.beans;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/6/4.
 */

public class UserConfig implements Serializable{
    String userId;
    int message;
    boolean fans;
    boolean comment;
    boolean privateMessage;

    public UserConfig(int message, boolean fans, boolean comment, boolean privateMessage) {
        this.message = message;
        this.fans = fans;
        this.comment = comment;
        this.privateMessage = privateMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public boolean isFans() {
        return fans;
    }

    public void setFans(boolean fans) {
        this.fans = fans;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isPrivateMessage() {
        return privateMessage;
    }

    public void setPrivateMessage(boolean privateMessage) {
        this.privateMessage = privateMessage;
    }
}
