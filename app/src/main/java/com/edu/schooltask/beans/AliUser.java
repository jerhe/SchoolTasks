package com.edu.schooltask.beans;

/**
 * Created by 夜夜通宵 on 2017/9/14.
 */

public class AliUser {
    private int id;
    private String aliId;
    private String aliName;

    public AliUser(String aliId, String aliName) {
        this.aliId = aliId;
        this.aliName = aliName;
    }

    public String getAliId() {
        return aliId;
    }

    public void setAliId(String aliId) {
        this.aliId = aliId;
    }

    public String getAliName() {
        return aliName;
    }

    public void setAliName(String aliName) {
        this.aliName = aliName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
