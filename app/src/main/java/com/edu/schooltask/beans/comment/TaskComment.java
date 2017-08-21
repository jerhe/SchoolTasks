package com.edu.schooltask.beans.comment;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class TaskComment extends Comment {
    String orderId;	//任务订单号

    String toName;  //回复对象昵称

    public TaskComment(){}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }
}
