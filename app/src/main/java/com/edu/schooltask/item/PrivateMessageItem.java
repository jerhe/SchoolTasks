package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.schooltask.beans.Poll;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/29.
 */

public class PrivateMessageItem implements MultiItemEntity,Serializable{
    public static int SYSTEM = 0;
    public static int RECEIVE = 1;
    public static int SEND = 2;

    Poll poll;
    boolean showTime;
    int itemType;

    public PrivateMessageItem(int itemType, Poll poll){
        this.itemType = itemType;
        this.poll = poll;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }
}
