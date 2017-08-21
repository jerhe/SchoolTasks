package com.edu.schooltask.beans.comment;

import com.edu.schooltask.beans.UserInfo;

/**
 * Created by 夜夜通宵 on 2017/8/20.
 */

public class Comment {
    long id;	//自增主键 id
    long parentId;	//顶层评论id
    String fromId;	//评论用户id
    String toId;	//评论对象id
    String comment;	//评论内容
    int replyCount;	//回复数
    String createTime;	//评论时间

    //评论人信息
    UserInfo userInfo;


    public Comment(){}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getToId() {
        return toId;
    }
    public void setToId(String toId) {
        this.toId = toId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getReplyCount() {
        return replyCount;
    }
    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
