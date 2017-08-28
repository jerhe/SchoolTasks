package com.edu.schooltask.beans;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import io.rong.push.common.ParcelUtils;

public class UserInfo extends DataSupport implements Serializable,Parcelable{
	String userId;
	String name;
	String school;
	String sign;
	int sex;
	String birth;
	String email;
	
	
	public UserInfo() {
		
	}

	public UserInfo(UserInfo user){
		this.userId = user.getUserId();
		this.name = user.getName();
		this.sign = user.getSign();
		this.school = user.getSchool();
		this.sex = user.getSex();
		this.birth = user.getBirth();
		this.email = user.getEmail();
	}

	public UserInfo(String userId, String name, String sign, String school, int sex, String birth) {
		super();
		this.userId = userId;
		this.name = name;
		this.sign = sign;
		this.school = school;
		this.sex = sex;
		this.birth = birth;
	}

	public UserInfo(Parcel in){
		setUserId(ParcelUtils.readFromParcel(in));
		setName(ParcelUtils.readFromParcel(in));
		setSchool(ParcelUtils.readFromParcel(in));
		setSign(ParcelUtils.readFromParcel(in));
		setSex(ParcelUtils.readIntFromParcel(in));
		setBirth(ParcelUtils.readFromParcel(in));
		setEmail(ParcelUtils.readFromParcel(in));
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

	@Override
	public boolean equals(Object obj) {
		return ((UserInfo)obj).getUserId().equals(userId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeToParcel(dest, userId);
		ParcelUtils.writeToParcel(dest, name);
		ParcelUtils.writeToParcel(dest, school);
		ParcelUtils.writeToParcel(dest, sign);
		ParcelUtils.writeToParcel(dest, sex);
		ParcelUtils.writeToParcel(dest, birth);
		ParcelUtils.writeToParcel(dest, email);
	}

	public static final Parcelable.Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
		@Override
		public UserInfo createFromParcel(Parcel source) {
			return new UserInfo(source);
		}

		@Override
		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
	};
}
