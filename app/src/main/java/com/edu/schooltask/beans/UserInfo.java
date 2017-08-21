package com.edu.schooltask.beans;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class UserInfo extends DataSupport implements Serializable{
	String userId;
	String name;
	String school;
	String sign;
	int sex;
	String birth;
	String email;
	int followerCount;
	int fansCount;
	
	
	public UserInfo() {
		
	}

	public UserInfo(UserInfo user){
		this.userId = user.getUserId();
		this.name = user.getName();
		this.sign = user.getSign();
		this.school = user.getSchool();
		this.sex = user.getSex();
		this.birth = user.getBirth();
		this.followerCount = user.getFollowerCount();
		this.fansCount = user.getFansCount();
		this.email = user.getEmail();
	}

	public UserInfo(String userId, String name, String sign, String school, int sex, String birth,
                    int followers, int fans) {
		super();
		this.userId = userId;
		this.name = name;
		this.sign = sign;
		this.school = school;
		this.sex = sex;
		this.birth = birth;
		this.followerCount = followers;
		this.fansCount = fans;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowers(int followerCount) {
		this.followerCount = followerCount;
	}

	public int getFansCount() {
		return fansCount;
	}

	public void setFans(int fansCount) {
		this.fansCount = fansCount;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UserInfo)obj).getUserId().equals(userId);
	}
}
