package com.edu.schooltask.item;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class TaskComment {
    long commentId;
    String orderId;
    String userId;
    String userName;
    String toUserName;
    String userSchool;
    int userSex;
    long parentId;
    String comment;
    int childCount;
    String commentTime;

    public TaskComment(long commentId, String orderId, String userId, String userName, String userSchool, int userSex, long parentId, String comment, int childCount, String commentTime) {
        this.commentId = commentId;
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userSchool = userSchool;
        this.userSex = userSex;
        this.parentId = parentId;
        this.comment = comment;
        this.childCount = childCount;
        this.commentTime = commentTime;
    }

    public TaskComment(long commentId, String orderId, String userId, String userName, String toUserName, String userSchool, int userSex, long parentId, String comment, int childCount, String commentTime) {
        this.commentId = commentId;
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.toUserName = toUserName;
        this.userSchool = userSchool;
        this.userSex = userSex;
        this.parentId = parentId;
        this.comment = comment;
        this.childCount = childCount;
        this.commentTime = commentTime;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
