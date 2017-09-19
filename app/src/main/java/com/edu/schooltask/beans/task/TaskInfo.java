package com.edu.schooltask.beans.task;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/8/19.
 */

public class TaskInfo implements Serializable{
    protected String orderId;	//订单号
    protected String description;	//类别
    protected String school;	//学校
    protected BigDecimal reward;	//报酬
    protected String content;	//内容
    protected String hideContent;   //隐藏内容
    protected int imageNum;	//图片数
    protected int lookCount;	//浏览数
    protected int commentCount;	//评论数

    public TaskInfo(){}

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
    public BigDecimal getReward() {
        return reward;
    }
    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getImageNum() {
        return imageNum;
    }
    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }
    public int getLookCount() {
        return lookCount;
    }
    public void setLookCount(int lookCount) {
        this.lookCount = lookCount;
    }
    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getHideContent() {
        return hideContent;
    }

    public void setHideContent(String hideContent) {
        this.hideContent = hideContent;
    }
}
