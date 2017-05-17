package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetOrderChildCommentEvent extends BaseHttpEvent{
    public GetOrderChildCommentEvent(){
        super();
    }

    public GetOrderChildCommentEvent(boolean ok){
        super(ok);
    }

    public GetOrderChildCommentEvent(String error){
        super(error);
    }
}
