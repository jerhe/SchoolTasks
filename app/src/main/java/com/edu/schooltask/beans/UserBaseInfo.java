package com.edu.schooltask.beans;

public class UserBaseInfo {
	String userId;
	String name;
	String school;
	int sex;
	
	
	public UserBaseInfo() {
		
	}


	public UserBaseInfo(String userId, String name, String school, int sex) {
		super();
		this.userId = userId;
		this.name = name;
		this.school = school;
		this.sex = sex;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSchool() {
		return school;
	}


	public void setSchool(String school) {
		this.school = school;
	}


	public int getSex() {
		return sex;
	}


	public void setSex(int sex) {
		this.sex = sex;
	}
	
	
	
}
