package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/8/20.
 */

public class Comment {
    protected int id;	//自增主键 id
    protected int parentId;	//顶层评论id
    protected String fromId;	//评论用户id
    protected String toName;  //回复对象昵称
    protected String comment;	//评论内容
    protected String image;
    protected int replyCount;	//回复数
    protected String createTime;	//评论时间

    //评论人信息
    protected UserInfo userInfo;



    public Comment(){}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getToName() {
        return toName;
    }
    public void setToName(String toName) {
        this.toName = toName;
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
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Comment && id == ((Comment)obj).getId();
    }
}
