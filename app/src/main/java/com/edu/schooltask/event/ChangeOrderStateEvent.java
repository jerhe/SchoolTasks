package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class ChangeOrderStateEvent extends BaseHttpEvent{
    public ChangeOrderStateEvent(){
        super();
    }

    public ChangeOrderStateEvent(boolean ok){
        super(ok);
    }

    public ChangeOrderStateEvent(String error){
        super(error);
    }
}
