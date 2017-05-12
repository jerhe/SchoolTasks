package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetSchoolOrderEvent extends BaseHttpEvent<User> {
    public GetSchoolOrderEvent() {
        super();
    }

    public GetSchoolOrderEvent(boolean ok) {
        super(ok);
    }
}
