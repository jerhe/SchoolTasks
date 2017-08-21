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

    public UserInfoWithToken(String userId, String token, String name, String school, String sign, int sex,
                             String birth, int follow, int fans) {
        super(userId, name, sign, school, sex, birth, follow, fans);
        this.token = token;
    }

    public UserInfoWithToken setUserInfo(UserInfo userInfo){
        this.userId = userInfo.getUserId();
        this.name = userInfo.getName();
        this.sign = userInfo.getSign();
        this.school = userInfo.getSchool();
        this.sex = userInfo.getSex();
        this.birth = userInfo.getBirth();
        this.followerCount = userInfo.getFollowerCount();
        this.fansCount = userInfo.getFansCount();
        return this;
    }

    public UserInfoWithToken(String userId, String name, int sex){
        this.userId = userId;
        this.name = name;
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}

