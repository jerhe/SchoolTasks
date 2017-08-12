package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class TaskComment {
    long id;	//评论ID
    String orderId;	//订单ID
    long parentId;	//父评论ID
    UserInfoBase commentUser;		//评论人信息
    UserInfoBase parentUser;		//父评论人信息
    String comment;	//评论内容
    String commentTime;	//评论时间
    int childCount;	//子评论数

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public UserInfoBase getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(UserInfoBase commentUser) {
        this.commentUser = commentUser;
    }

    public UserInfoBase getParentUser() {
        return parentUser;
    }

    public void setParentUser(UserInfoBase parentUser) {
        this.parentUser = parentUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
}
