package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class DeleteImageEvent {
    int index;
    public DeleteImageEvent(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
