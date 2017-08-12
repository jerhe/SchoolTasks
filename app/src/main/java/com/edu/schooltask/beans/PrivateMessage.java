package com.edu.schooltask.beans;

import android.util.Log;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.schooltask.beans.Poll;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/29.
 */

public class PrivateMessage extends DataSupport implements MultiItemEntity,Serializable{
    public static int SYSTEM = 0;
    public static int RECEIVE = 1;
    public static int SEND = 2;

    Poll poll;
    boolean showTime;
    int itemType;
    boolean hasRead;

    public PrivateMessage(int itemType, Poll poll){
        this.itemType = itemType;
        this.poll = poll;
        if (itemType == SEND) hasRead = true;
    }

    public PrivateMessage(Poll poll){
        this.itemType = poll.getType() == 0 ? PrivateMessage.SYSTEM : PrivateMessage.RECEIVE;
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

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }
}
