package com.edu.schooltask.item;

import com.edu.schooltask.beans.UserInfoBase;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class TaskItem implements Serializable{
    String orderId;
    BigDecimal cost;
    String school;
    String description;
    String content;
    String releaseTime;
    int imageNum;
    UserInfoBase user;

    public TaskItem() {}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public UserInfoBase getUser() {
        return user;
    }

    public void setUser(UserInfoBase user) {
        this.user = user;
    }
}
