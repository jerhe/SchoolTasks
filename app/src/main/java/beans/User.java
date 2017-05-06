package beans;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class User extends DataSupport{
    private String userId;
    private String token;
    private String name;
    private String school;
    private String sign;
    private int sex;
    private String birth;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}
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

    public User(){}

    public User(String userId, String token, String name, String school, String sign, int sex, String birth) {
        super();
        this.userId = userId;
        this.token = token;
        this.name = name;
        this.school = school;
        this.sign = sign;
        this.sex = sex;
        this.birth = birth;
    }

    public static User jsonObjectToUser(JSONObject data){
        try {
            String id = data.getString("userid");
            String token = data.getString("token");
            String name = data.getString("name");
            String school = data.getString("school");
            String sign = data.getString("sign");
            Log.e("SIGN",sign);
            int sex = data.getInt("sex");
            String birth = data.getString("birth");
            User user = new User(id, token, name, school, sign, sex, birth);
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

