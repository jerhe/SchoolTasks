package com.edu.schooltask.beans.comment;

import com.edu.schooltask.beans.comment.TaskComment;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/7/12.
 */

public class TaskCommentList {
    String orderId;
    List<TaskComment> taskComments;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }
}
