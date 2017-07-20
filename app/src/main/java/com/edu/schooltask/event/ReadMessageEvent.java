package com.edu.schooltask.event;

import com.edu.schooltask.beans.Poll;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/29.
 */

public class ReadMessageEvent {
    Poll poll;

    public ReadMessageEvent(){}

    public ReadMessageEvent(Poll poll){
        this.poll = poll;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPolls(Poll poll) {
        this.poll = poll;
    }
}
