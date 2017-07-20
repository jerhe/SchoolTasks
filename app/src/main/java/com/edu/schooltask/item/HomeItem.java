package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.edu.schooltask.beans.TaskComment;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeItem implements MultiItemEntity, Serializable {
    public final static int LOAD_TIP = 0;
    public final static int TASK_ITEM = 1;
    public final static int TASK_INFO_ITEM = 2;
    public final static int COUNT_ITEM = 3;
    public final static int COMMENT = 4;

    private int itemType;
    TaskItem taskItem;
    TaskCountItem taskCountItem;
    String tip;
    TaskComment taskComment;
    public long parentId;  //用于判断是否是子回复

    public HomeItem(int itemType){
        this.itemType = itemType;
    }


    public HomeItem(int itemType, TaskItem taskItem){
        this.taskItem = taskItem;
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

    //count
    public HomeItem(int itemType, TaskCountItem taskCountItem){
        this.itemType = itemType;
        this.taskCountItem = taskCountItem;
    }

    //---------------------------------------------------------------

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public TaskItem getTaskItem() {
        return taskItem;
    }

    public void setTaskItem(TaskItem taskItem) {
        this.taskItem = taskItem;
    }

    public TaskCountItem getTaskCountItem() {
        return taskCountItem;
    }

    public void setTaskCountItem(TaskCountItem taskCountItem) {
        this.taskCountItem = taskCountItem;
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
