package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetUserOrderEvent extends BaseHttpEvent<User> {
    public GetUserOrderEvent() {
        super();
    }

    public GetUserOrderEvent(boolean ok) {
        super(ok);
    }
}
