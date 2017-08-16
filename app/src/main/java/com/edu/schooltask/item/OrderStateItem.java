package com.edu.schooltask.item;

import com.edu.schooltask.beans.UserInfoBase;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class OrderStateItem {
    boolean isFinish;
    String name;
    String text;
    boolean isAccept;
    UserInfoBase acceptUser;
    boolean isMe;

    public OrderStateItem(boolean isFinish, String name, String text) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = false;
    }

    //被接单状态
    public OrderStateItem(boolean isFinish, String name, String text, UserInfoBase acceptUser, boolean isMe) {
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

    public UserInfoBase getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(UserInfoBase acceptUser) {
        this.acceptUser = acceptUser;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
