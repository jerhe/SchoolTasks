package com.edu.schooltask.item;

import com.edu.schooltask.beans.User;

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

    String id;
    int type;
    String title;
    String content;
    float cost;
    int state;
    String school;
    String releaseTime;
    String stateStr;
    int imageNum;
    User releaseUser;

    public OrderItem(){

    }

    public OrderItem(String id, int type, String title, String content, float cost, int state){
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


    //用于待评价订单
    public OrderItem(String id, String title, String content, String releaseTime){
        this.id = id;
        this.title = title;
        this.content = content;
        this.releaseTime = releaseTime;
    }

    //用于首页附近任务订单
    public OrderItem(String id, String school, String content, float cost, String releaseTime, int imageNum, User user){
        this.id = id;
        this.content = content;
        this.cost = cost;
        this.school = school;
        this.releaseTime = releaseTime;
        this.imageNum = imageNum;
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

    public User getReleaseUser(){
        return this.releaseUser;
    }

    public void setReleaseUser(User user){
        this.releaseUser = user;
    }
}
