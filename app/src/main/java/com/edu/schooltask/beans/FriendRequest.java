package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class FriendRequest{
    UserInfo userInfo;
    int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
