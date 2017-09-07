package com.edu.schooltask.beans;

import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class Detail {
    String id;
    String user_id;
    BigDecimal money;
    int type;
    String explanation;
    String createTime;

    public Detail(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Detail)) return false;
        return ((Detail)obj).getId() == getId();
    }
}
