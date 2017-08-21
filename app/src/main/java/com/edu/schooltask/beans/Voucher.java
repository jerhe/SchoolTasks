package com.edu.schooltask.beans;

import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/8/17.
 */

public class Voucher {
    private long id;	//代金券编号
    private String userId;	//所属用户
    private BigDecimal money;	//金额
    private String explanation;	//说明
    private boolean available;	//有效性
    private String ExpireTime;	//到期时间
    private String CreateTime;	//获得时间

    public Voucher(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getExpireTime() {
        return ExpireTime;
    }

    public void setExpireTime(String expireTime) {
        ExpireTime = expireTime;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}
