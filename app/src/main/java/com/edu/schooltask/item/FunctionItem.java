package com.edu.schooltask.item;



/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class FunctionItem {
    private int resId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public FunctionItem(int resId, String name){
        this.resId = resId;
        this.name = name;
    }
}
