package com.edu.schooltask.item;

import com.edu.schooltask.beans.UserInfo;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class OrderStateItem {
    boolean isFinish;
    String name;
    String text;
    boolean isAccept;
    UserInfo acceptUser;

    public OrderStateItem(boolean isFinish, String name, String text) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = false;
    }

    //被接单状态
    public OrderStateItem(boolean isFinish, String name, String text, UserInfo acceptUser) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = true;
        this.acceptUser = acceptUser;
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

    public UserInfo getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(UserInfo acceptUser) {
        this.acceptUser = acceptUser;
    }
}
