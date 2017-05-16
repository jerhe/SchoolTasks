package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class ReleaseEvent extends BaseHttpEvent{
    public ReleaseEvent(){
        super();
    }

    public ReleaseEvent(boolean ok){
        super(ok);
    }

    public ReleaseEvent(String error){
        super(error);
    }
}
