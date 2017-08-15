package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.schooltask.beans.TaskComment;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeItem implements MultiItemEntity, Serializable {
    public final static int LOAD_TIP = 0;
    public final static int COMMENT = 1;

    private int itemType;
    String tip;
    TaskComment taskComment;
    public long parentId;  //用于判断是否是子回复

    public HomeItem(int itemType){
        this.itemType = itemType;
    }



    public HomeItem(int itemType, TaskComment taskComment){
        this.taskComment = taskComment;
        this.itemType = itemType;
    }

    public HomeItem(int itemType, TaskComment taskComment, long parentId){
        this.taskComment = taskComment;
        this.itemType = itemType;
        this.parentId = parentId;
    }

    //loadtip
    public HomeItem(int itemType, String tip){
        this.itemType = itemType;
        this.tip = tip;
    }

    //---------------------------------------------------------------

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public TaskComment getTaskComment() {
        return taskComment;
    }

    public void setTaskComment(TaskComment taskComment) {
        this.taskComment = taskComment;
    }
}
