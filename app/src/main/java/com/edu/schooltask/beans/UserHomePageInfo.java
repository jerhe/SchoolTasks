package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/7/13.
 */

public class UserHomePageInfo {
    UserInfoBase userInfoBase;
    int relationType;

    public UserInfoBase getUserInfoBase() {
        return userInfoBase;
    }

    public void setUserInfoBase(UserInfoBase userInfoBase) {
        this.userInfoBase = userInfoBase;
    }

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }
}
