package com.edu.schooltask.beans.task;

/**
 * Created by 夜夜通宵 on 2017/8/19.
 */

public class TaskOrderItem extends TaskInfo {
    String releaseId;
    String limitTime;
    int state;  //订单状态
    int type;   //发布/接单

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }
}
