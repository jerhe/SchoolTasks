package com.edu.schooltask.other;

import com.edu.schooltask.item.MessageItem;
import com.edu.schooltask.utils.DateUtil;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by 夜夜通宵 on 2017/5/29.
 */

public class MessageComparator implements Comparator<MessageItem> {
    @Override
    public int compare(MessageItem o1, MessageItem o2) {
        Calendar c1 = DateUtil.stringToCalendar(o1.getLastTime());
        Calendar c2 = DateUtil.stringToCalendar(o2.getLastTime());
        if(c1.after(c2)) return -1;
        if(c1.before(c2)) return 1;
        return 0;
    }
}
