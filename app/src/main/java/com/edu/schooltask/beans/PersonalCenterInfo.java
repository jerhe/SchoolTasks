package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/7/13.
 */

public class PersonalCenterInfo {
    boolean hasPayPwd;
    int credit;
    String registerTime;

    public boolean isHasPayPwd() {
        return hasPayPwd;
    }

    public void setHasPayPwd(boolean hasPayPwd) {
        this.hasPayPwd = hasPayPwd;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}
