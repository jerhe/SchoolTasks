package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/9/19.
 */

public class DeleteDynamicEvent {
    private int dynamicId;

    public DeleteDynamicEvent(int dynamicId) {
        this.dynamicId = dynamicId;
    }

    public int getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(int dynamicId) {
        this.dynamicId = dynamicId;
    }
}
