package com.edu.schooltask.item;

import com.edu.schooltask.beans.UserBaseInfo;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class StateItem {
    boolean isFinish;
    String name;
    String text;
    boolean isAccept;
    UserBaseInfo acceptUser;
    boolean isMe;

    public StateItem(boolean isFinish, String name, String text) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = false;
    }

    //被接单状态
    public StateItem(boolean isFinish, String name, String text, UserBaseInfo acceptUser, boolean isMe) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = true;
        this.acceptUser = acceptUser;
        this.isMe = isMe;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    public UserBaseInfo getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(UserBaseInfo acceptUser) {
        this.acceptUser = acceptUser;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
