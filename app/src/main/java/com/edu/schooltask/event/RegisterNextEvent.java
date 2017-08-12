package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/8/2.
 */

public class RegisterNextEvent {
    private String id;

    public RegisterNextEvent(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
