package com.edu.schooltask.item;

import org.litepal.crud.DataSupport;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class OrderWaitAssessItem extends DataSupport{

    String id;
    String title;
    String time;
    public OrderWaitAssessItem(){

    }

    public OrderWaitAssessItem(String id, String title, String time){
        this.id = id;
        this.title = title;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
