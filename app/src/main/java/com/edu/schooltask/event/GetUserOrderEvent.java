package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetUserOrderEvent extends BaseHttpEvent<User> {
    public int type = 0;
    public GetUserOrderEvent() {
        super();
    }

    public GetUserOrderEvent(int type){
        this.type = type;
    }
    public GetUserOrderEvent(boolean ok, int type) {
        super(ok);
        this.type = type;
    }
}
