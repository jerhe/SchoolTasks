package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class SetPaypwdEvent extends BaseHttpEvent{
    public SetPaypwdEvent(){
        super();
    }

    public SetPaypwdEvent(boolean ok){
        super(ok);
    }
}
