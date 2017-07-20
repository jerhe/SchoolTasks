package com.edu.schooltask.beans;

import java.io.Serializable;

public class UserBaseInfo implements Serializable{
	String userId;
	String name;
	String school;
	String sign;
	int sex;
	String birth;
	int follow;
	int fans;
	
	
	public UserBaseInfo() {
		
	}

	public UserBaseInfo(UserBaseInfo user){
		this.userId = user.getUserId();
		this.name = user.getName();
		this.sign = user.getSign();
		this.school = user.getSchool();
		this.sex = user.getSex();
		this.birth = user.getBirth();
		this.follow = user.getFollow();
		this.fans = user.getFans();
	}

	public UserBaseInfo(String userId, String name, String sign, String school, int sex, String birth,
						int follow, int fans) {
		super();
		this.userId = userId;
		this.name = name;
		this.sign = sign;
		this.school = school;
		this.sex = sex;
		this.birth = birth;
		this.follow = follow;
		this.fans = fans;
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

	public int getFollow() {
		return follow;
	}

	public void setFollow(int follow) {
		this.follow = follow;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UserBaseInfo)obj).getUserId().equals(userId);
	}
}
