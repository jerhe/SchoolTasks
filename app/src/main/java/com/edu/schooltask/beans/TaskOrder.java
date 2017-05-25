package com.edu.schooltask.beans;

import java.math.BigDecimal;


public class TaskOrder {
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

	String orderId;
	String school;
	String description;
	UserBaseInfo releaseUser;
	String limitTime;
	String content;
	UserBaseInfo acceptUser;
	BigDecimal cost;
	int state;
	String releaseTime;
	String acceptTime;
	String finishTime;
	String assessTime;
	String overTime;
	String abandonTime;
	String cancelTime;
	int lookCount;
	int score;
	String assess;
	int imageNum;

    public int type;   //发布方和接受方
	String stateStr;

	public TaskOrder(){}

	public void initStateStr(){
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserBaseInfo getReleaseUser() {
		return releaseUser;
	}

	public void setReleaseUser(UserBaseInfo releaseUser) {
		this.releaseUser = releaseUser;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserBaseInfo getAcceptUser() {
		return acceptUser;
	}

	public void setAcceptUser(UserBaseInfo acceptUser) {
		this.acceptUser = acceptUser;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getAssessTime() {
		return assessTime;
	}

	public void setAssessTime(String assessTime) {
		this.assessTime = assessTime;
	}

	public String getOverTime() {
		return overTime;
	}

	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public String getAbandonTime() {
		return abandonTime;
	}

	public void setAbandonTime(String abandonTime) {
		this.abandonTime = abandonTime;
	}

	public String getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}

	public int getLookCount() {
		return lookCount;
	}

	public void setLookCount(int lookCount) {
		this.lookCount = lookCount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getAssess() {
		return assess;
	}

	public void setAssess(String assess) {
		this.assess = assess;
	}

	public int getImageNum() {
		return imageNum;
	}

	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}

	public String getStateStr() {
		return stateStr;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}
}