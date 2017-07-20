package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/30.
 */

public class UnreadMessageNumEvent {
    int num;
    public UnreadMessageNumEvent(int num){
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
