package com.edu.schooltask.item;

import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.UserBaseInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class MessageItem implements Serializable{
    UserBaseInfo user;  //消息来源
    List<Poll> polls;   //未读消息
    Poll lastPoll;  //最新消息
    String lastTime;    //最新未读消息的时间

    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public UserBaseInfo getUser() {
        return user;
    }

    public void setUser(UserBaseInfo user) {
        this.user = user;
    }

    public Poll getLastPoll() {
        return lastPoll;
    }

    public void setLastPoll(Poll lastPoll) {
        this.lastPoll = lastPoll;
    }
}
