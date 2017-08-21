package com.edu.schooltask.beans.task;

import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/8/19.
 */

public class TaskOrder {
    long id;	//自增主键
    String orderId;	//订单号
    String releaseId;	//发布用户id
    String acceptId;	//接单用户id
    BigDecimal cost;	//支付金额
    long voucherId;	//代金券编号
    String limitTime;	//订单时限
    int state;	//订单状态
    String releaseTime;	//发布时间
    String acceptTime;	//接单时间
    String finishedTime;	//完成时间
    String overTime;	//超时时间
    String abandonTime;	//放弃时间
    String cancelTime;	//取消时间

    public TaskOrder(){}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(String acceptId) {
        this.acceptId = acceptId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(long voucherId) {
        this.voucherId = voucherId;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(String finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    public String getAbandonTime() {
        return abandonTime;
    }

    public void setAbandonTime(String abandonTime) {
        this.abandonTime = abandonTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }
}
