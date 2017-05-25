package server.api.login;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class LoginEvent extends BaseEvent {
    String token;
    String userId;
    String name;
    String school;
    String sign;
    int sex;
    String birth;

    public LoginEvent(){}

    public LoginEvent(boolean ok, int code, String error){
        super(ok, code, error);
    }

    public LoginEvent(boolean ok, int code, String error, String token, String userId, String name, String school, String sign, int sex, String birth) {
        super(ok, code, error);
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.school = school;
        this.sign = sign;
        this.sex = sex;
        this.birth = birth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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
