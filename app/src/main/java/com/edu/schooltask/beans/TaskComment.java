package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class TaskComment {
    private long id;	//评论ID
    private String orderId;	//订单ID
    private long parentId;	//父评论ID
    private String userId;  //评论人id
    private String toUserId;    //评论回复人id
    private UserInfoBase commentUser;		//评论人信息
    private UserInfoBase toUser;		//父评论人信息
    private String comment;	//评论内容
    private int replyCount; //回复数
    private String commentTime;	//评论时间

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

    public UserInfoBase getToUser() {
        return toUser;
    }

    public void setToUser(UserInfoBase toUser) {
        this.toUser = toUser;
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

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
