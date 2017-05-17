package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetOrderCommentEvent extends BaseHttpEvent{
    public GetOrderCommentEvent(){
        super();
    }

    public GetOrderCommentEvent(boolean ok){
        super(ok);
    }

    public GetOrderCommentEvent(String error){
        super(error);
    }
}
