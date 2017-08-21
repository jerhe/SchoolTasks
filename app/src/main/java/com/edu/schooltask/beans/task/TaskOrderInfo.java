package com.edu.schooltask.beans.task;


import com.edu.schooltask.beans.UserInfo;

public class TaskOrderInfo extends TaskOrder{
	TaskInfo taskInfo;	//任务信息

	//发布人信息
	UserInfo releaseUserInfo;

	//接单人信息
	UserInfo acceptUserInfo;

	public TaskOrderInfo(){
		super();
	}

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

	public UserInfo getReleaseUserInfo() {
		return releaseUserInfo;
	}

	public void setReleaseUserInfo(UserInfo releaseUserInfo) {
		this.releaseUserInfo = releaseUserInfo;
	}

	public UserInfo getAcceptUserInfo() {
		return acceptUserInfo;
	}

	public void setAcceptUserInfo(UserInfo acceptUserInfo) {
		this.acceptUserInfo = acceptUserInfo;
	}

	public TaskItem toTaskItem(){
		TaskItem taskItem = new TaskItem();
		taskItem.setUserInfo(getReleaseUserInfo());
		taskItem.setLimitTime(getLimitTime());
		taskItem.setReleaseTime(getReleaseTime());
		TaskInfo taskInfo = getTaskInfo();
		taskItem.setCommentCount(taskInfo.getCommentCount());
		taskItem.setContent(taskInfo.getContent());
		taskItem.setDescription(taskInfo.getDescription());
		taskItem.setImageNum(taskInfo.getImageNum());
		taskItem.setSchool(taskInfo.getSchool());
		taskItem.setReward(taskInfo.getReward());
		taskItem.setOrderId(getOrderId());
		taskItem.setLookCount(taskInfo.getLookCount());
		return taskItem;
	}
}