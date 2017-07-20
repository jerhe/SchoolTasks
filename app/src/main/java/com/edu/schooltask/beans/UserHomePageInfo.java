package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/7/13.
 */

public class UserHomePageInfo {
    UserBaseInfo userBaseInfo;
    int relationType;

    public UserBaseInfo getUserBaseInfo() {
        return userBaseInfo;
    }

    public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
        this.userBaseInfo = userBaseInfo;
    }

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }
}
