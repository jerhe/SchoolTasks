package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class GetAssessOrderEvent extends BaseHttpEvent<User> {

    public GetAssessOrderEvent() {
        super();
    }

    public GetAssessOrderEvent(boolean ok){
        super(ok);
    }
}
