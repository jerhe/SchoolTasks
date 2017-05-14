package com.edu.schooltask.item;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class StateItem {
    boolean isFinish;
    String name;
    String text;
    boolean isAccept;
    String acceptUserId;
    String acceptUserName;
    int acceptUserSex;
    String acceptUserSign;

    public StateItem(boolean isFinish, String name, String text) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = false;
    }

    //被接单状态
    public StateItem(boolean isFinish, String name, String text, String acceptUserId,
                     String acceptUserName, int acceptUserSex, String acceptUserSign) {
        this.isFinish = isFinish;
        this.name = name;
        this.text = text;
        isAccept = true;
        this.acceptUserId = acceptUserId;
        this.acceptUserName = acceptUserName;
        this.acceptUserSex = acceptUserSex;
        this.acceptUserSign = acceptUserSign;
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

    public String getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public String getAcceptUserName() {
        return acceptUserName;
    }

    public void setAcceptUserName(String acceptUserName) {
        this.acceptUserName = acceptUserName;
    }

    public int getAcceptUserSex() {
        return acceptUserSex;
    }

    public void setAcceptUserSex(int acceptUserSex) {
        this.acceptUserSex = acceptUserSex;
    }

    public String getAcceptUserSign() {
        return acceptUserSign;
    }

    public void setAcceptUserSign(String acceptUserSign) {
        this.acceptUserSign = acceptUserSign;
    }
}
