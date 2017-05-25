package com.edu.schooltask.item;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class TaskCountItem implements Serializable {
    int lookCount;
    int commentCount;

    public TaskCountItem() {}

    public TaskCountItem(int commentCount, int lookCount) {
        this.commentCount = commentCount;
        this.lookCount = lookCount;
    }

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
