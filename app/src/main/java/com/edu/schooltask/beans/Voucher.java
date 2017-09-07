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
    private String expireTime;	//到期时间
    private String createTime;	//获得时间

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
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        expireTime = expireTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        createTime = createTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Voucher)) return false;
        return ((Voucher)obj).getId() == getId();
    }
}
