package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetOrderInfoEvent extends BaseHttpEvent<User> {
    public GetOrderInfoEvent() {
        super();
    }


    public GetOrderInfoEvent(boolean ok) {
        super(ok);
    }
}
