package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class MessageCountEvent {
    int count;
    public MessageCountEvent(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
