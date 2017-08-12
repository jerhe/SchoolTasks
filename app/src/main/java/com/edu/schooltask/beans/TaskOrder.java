package com.edu.schooltask.beans;

import java.math.BigDecimal;


public class TaskOrder {

	String orderId;
	String school;
	String description;
	String releaseId;
	String limitTime;
	String content;
	String acceptId;
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

	UserInfoBase releaseUser;
	UserInfoBase acceptUser;
    public int type;   //发布方和接受方
	String stateStr;

	public TaskOrder(){}

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

	public UserInfoBase getReleaseUser() {
		return releaseUser;
	}

	public void setReleaseUser(UserInfoBase releaseUser) {
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

	public UserInfoBase getAcceptUser() {
		return acceptUser;
	}

	public void setAcceptUser(UserInfoBase acceptUser) {
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

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(String acceptId) {
        this.acceptId = acceptId;
    }

    public String getStateStr() {
		return stateStr;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}
}