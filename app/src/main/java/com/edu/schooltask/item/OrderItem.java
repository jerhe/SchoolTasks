package com.edu.schooltask.item;

import com.edu.schooltask.beans.User;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class OrderItem implements Serializable{

    public final static int STATE_WAIT_ACCEPT = 0;
    public final static int STATE_WAIT_FINISH = 1;
    public final static int STATE_WAIT_CONFIRM = 2;
    public final static int STATE_WAIT_ASSESS= 3;
    public final static int STATE_FINISH_ASSESS= 4;
    public final static int STATE_UNLESS = 5;
    public final static int STATE_CANCEL = 6;
    public final static int STATE_ABANDON = 7;
    public final static int STATE_OVERTIME = 8;

    public final static String STATE_WAIT_ACCEPT_STR = "待接单";
    public final static String STATE_WAIT_FINISH_STR = "待完成";
    public final static String STATE_WAIT_CONFIRM_STR = "待确认";
    public final static String STATE_WAIT_ASSESS_STR = "待评价";
    public final static String STATE_FINISH_ASSESS_STR = "已评价";
    public final static String STATE_UNLESS_STR = "已失效";
    public final static String STATE_CANCEL_STR = "已取消";
    public final static String STATE_ABANDON_STR = "已放弃";
    public final static String STATE_OVERTIME_STR = "已超时";

    String id;
    int type;
    String content;
    float cost;
    int state;
    String school;
    String releaseTime;
    String stateStr;
    int imageNum;
    int lookNum;
    User releaseUser;

    public OrderItem(){

    }

    public OrderItem(String id, int type, String content, float cost, int state){
        this.id = id;
        this.type = type;
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
            case STATE_WAIT_CONFIRM:
                stateStr = STATE_WAIT_CONFIRM_STR;
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
            case STATE_ABANDON:
                stateStr = STATE_ABANDON_STR;
                break;
            case STATE_OVERTIME:
                stateStr = STATE_OVERTIME_STR;
                break;
        }
    }


    //用于待评价订单
    public OrderItem(String id, int type, String content, String releaseTime,int cost){
        this.id = id;
        this.type = type;
        this.content = content;
        this.releaseTime = releaseTime;
        this.cost = cost;
    }

    //用于首页附近任务订单
    public OrderItem(String id, String school, String content, float cost, String releaseTime,
                     int imageNum, int lookNum, User user){
        this.id = id;
        this.content = content;
        this.cost = cost;
        this.school = school;
        this.releaseTime = releaseTime;
        this.imageNum = imageNum;
        this.lookNum = lookNum;
        this.releaseUser = user;
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

    public void setSchool(String school){
        this.school =school;
    }

    public String getSchool(){
        return school;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getReleaseTime(){
        return this.releaseTime;
    }

    public void  setReleaseTime(String releaseTime){
        this.releaseTime = releaseTime;
    }

    public int getImageNum(){
        return imageNum;
    }

    public void setImageNum(int imageNum){
        this.imageNum = imageNum;
    }

    public int getLookNum(){
        return lookNum;
    }

    public void setLookNum(int lookNum){
        this.lookNum = lookNum;
    }
    public User getReleaseUser(){
        return this.releaseUser;
    }

    public void setReleaseUser(User user){
        this.releaseUser = user;
    }
}
