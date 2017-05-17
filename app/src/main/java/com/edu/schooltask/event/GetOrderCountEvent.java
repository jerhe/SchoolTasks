package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetOrderCountEvent extends BaseHttpEvent{
    public GetOrderCountEvent(){
        super();
    }

    public GetOrderCountEvent(boolean ok){
        super(ok);
    }

    public GetOrderCountEvent(String error){
        super(error);
    }
}
