package com.edu.schooltask.event;

/**
 * Created by 夜夜通宵 on 2017/9/11.
 */

public class LikeChangedEvent {

    private long id;
    private boolean like;

    public LikeChangedEvent(long id, boolean like){
        this.id = id;
        this.like = like;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
