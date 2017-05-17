package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class NewOrderCommentEvent extends BaseHttpEvent{
    public NewOrderCommentEvent(){
        super();
    }

    public NewOrderCommentEvent(boolean ok){
        super(ok);
    }

    public NewOrderCommentEvent(String error){
        super(error);
    }
}
