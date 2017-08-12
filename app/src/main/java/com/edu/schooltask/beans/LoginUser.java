package com.edu.schooltask.beans;

import com.edu.schooltask.utils.DateUtil;

/**
 * Created by 夜夜通宵 on 2017/8/11.
 */

public class LoginUser extends UserInfo {
    String loginTime;

    public LoginUser(){}

    public LoginUser(UserInfo userInfo){
        this.userId = userInfo.getUserId();
        this.name = userInfo.getName();
        this.sign = userInfo.getSign();
        this.school = userInfo.getSchool();
        this.sex = userInfo.getSex();
        this.birth = userInfo.getBirth();
        this.followers = userInfo.getFollowers();
        this.fans = userInfo.getFans();
        this.token = userInfo.getToken();
        this.loginTime = DateUtil.getNow();
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }
}
