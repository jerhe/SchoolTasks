package com.edu.schooltask.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class User extends UserBaseInfo{
    String token;

    public User(){}

    public User(UserBaseInfo userBaseInfo){
        super(userBaseInfo);
    }

    public User(String userId,String name){
        this.userId = userId;
        this.name = name;
    }

    public User(String userId, String token, String name, String school, String sign, int sex,
                String birth, int follow, int fans) {
        super(userId, name, sign, school, sex, birth, follow, fans);
        this.token = token;
    }

    public User setUserInfo(UserBaseInfo userBaseInfo){
        this.userId = userBaseInfo.getUserId();
        this.name = userBaseInfo.getName();
        this.sign = userBaseInfo.getSign();
        this.school = userBaseInfo.getSchool();
        this.sex = userBaseInfo.getSex();
        this.birth = userBaseInfo.getBirth();
        this.follow = userBaseInfo.getFollow();
        this.fans = userBaseInfo.getFans();
        return this;
    }

    public User(String userId, String name, int sex){
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

