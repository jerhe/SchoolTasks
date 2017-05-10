package com.edu.schooltask.item;

import org.litepal.crud.DataSupport;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class OrderItem {

    public final static int STATE_WAIT_ACCEPT = 0;
    public final static int STATE_WAIT_FINISH = 1;
    public final static int STATE_WAIT_ASSESS= 2;
    public final static int STATE_FINISH_ASSESS= 3;
    public final static int STATE_UNLESS = 4;
    public final static int STATE_CANCEL = 5;

    public final static String STATE_WAIT_ACCEPT_STR = "待接单";
    public final static String STATE_WAIT_FINISH_STR = "待完成";
    public final static String STATE_WAIT_ASSESS_STR = "待评价";
    public final static String STATE_FINISH_ASSESS_STR = "已评价";
    public final static String STATE_UNLESS_STR = "已失效";
    public final static String STATE_CANCEL_STR = "已取消";

    int index;
    String id;
    int type;
    String title;
    String content;
    float cost;
    int state;
    String stateStr;

    public OrderItem(){

    }

    public OrderItem(int index, String id, int type, String title, String content, float cost, int state){
        this.index = index;
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.cost = cost;
        this.state = state;
        switch (state){
            case STATE_WAIT_ACCEPT:
                stateStr = STATE_WAIT_ACCEPT_STR;
                break;
            case STATE_WAIT_FINISH:
                stateStr = STATE_WAIT_FINISH_STR;
                break;
            case STATE_WAIT_ASSESS:
                stateStr = STATE_WAIT_ASSESS_STR;
                break;
            case STATE_FINISH_ASSESS:
                stateStr = STATE_FINISH_ASSESS_STR;
                break;
            case STATE_CANCEL:
                stateStr = STATE_CANCEL_STR;
                break;
            case STATE_UNLESS:
                stateStr = STATE_UNLESS_STR;
                break;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getStateStr() {
        return stateStr;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
