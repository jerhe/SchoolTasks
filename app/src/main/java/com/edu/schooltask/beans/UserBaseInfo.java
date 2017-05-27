package com.edu.schooltask.beans;

public class UserBaseInfo {
	String userId;
	String name;
	String school;
	String sign;
	int sex;
	String birth;
	
	
	public UserBaseInfo() {
		
	}


	public UserBaseInfo(String userId, String name, String sign, String school, int sex, String birth) {
		super();
		this.userId = userId;
		this.name = name;
		this.sign = sign;
		this.school = school;
		this.sex = sex;
		this.birth = birth;
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

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}
}
