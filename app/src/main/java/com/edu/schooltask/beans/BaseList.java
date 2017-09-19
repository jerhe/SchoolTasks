package com.edu.schooltask.beans;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/16.
 */

public class BaseList<T> {
    protected String sid;
    protected int iid;
    protected List<T> list;
    protected boolean more;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
