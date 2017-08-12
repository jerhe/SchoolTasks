package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class UserInfo extends UserInfoBase {
    String token;

    public UserInfo(){}

    public UserInfo(UserInfoBase userInfoBase){
        super(userInfoBase);
    }

    public UserInfo(String userId, String name){
        this.userId = userId;
        this.name = name;
    }

    public UserInfo(String userId, String token, String name, String school, String sign, int sex,
                    String birth, int follow, int fans) {
        super(userId, name, sign, school, sex, birth, follow, fans);
        this.token = token;
    }

    public UserInfo setUserInfo(UserInfoBase userInfoBase){
        this.userId = userInfoBase.getUserId();
        this.name = userInfoBase.getName();
        this.sign = userInfoBase.getSign();
        this.school = userInfoBase.getSchool();
        this.sex = userInfoBase.getSex();
        this.birth = userInfoBase.getBirth();
        this.followers = userInfoBase.getFollowers();
        this.fans = userInfoBase.getFans();
        return this;
    }

    public UserInfo(String userId, String name, int sex){
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

