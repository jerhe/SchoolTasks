package com.edu.schooltask.event;

import com.edu.schooltask.beans.User;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class TabSelectedEvent{
    public int position;
    public TabSelectedEvent(int position) {
        this.position = position;
    }

}
