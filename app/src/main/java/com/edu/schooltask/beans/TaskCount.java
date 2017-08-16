package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/7/13.
 */

//用于获取服务端返回的评论数和浏览数
public class TaskCount {
    private int lookCount;
    private int commentCount;

    public int getLookCount() {
        return lookCount;
    }

    public void setLookCount(int lookCount) {
        this.lookCount = lookCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
