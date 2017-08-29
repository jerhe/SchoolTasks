package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class UserInfoWithToken extends UserInfo {
    String token;

    public UserInfoWithToken(){}

    public UserInfoWithToken(UserInfo userInfo){
        super(userInfo);
    }

    public UserInfoWithToken(String userId, String name){
        this.userId = userId;
        this.name = name;
    }

    public UserInfoWithToken(String userId, String token, String name, String head, String bg,
                             String school, String sign, int sex, String birth) {
        super(userId, name, head, bg, sign, school, sex, birth);
        this.token = token;
    }

    public UserInfoWithToken setUserInfo(UserInfo userInfo){
        this.userId = userInfo.getUserId();
        this.name = userInfo.getName();
        this.head = userInfo.getHead();
        this.bg = userInfo.getBg();
        this.sign = userInfo.getSign();
        this.school = userInfo.getSchool();
        this.sex = userInfo.getSex();
        this.birth = userInfo.getBirth();
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

