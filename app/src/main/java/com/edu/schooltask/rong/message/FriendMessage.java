package com.edu.schooltask.rong.message;

import android.os.Parcel;
import android.util.Log;

import com.edu.schooltask.beans.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.push.common.ParcelUtils;

/**
 * 订单消息
 * Created by 夜夜通宵 on 2017/8/25.
 */

@MessageTag(value = "ST:Friend",  flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class FriendMessage extends MessageContent{
    private String message; //消息内容

    public FriendMessage(String message) {
        this.message = message;
    }

    public FriendMessage(Parcel in) {
        setMessage(ParcelUtils.readFromParcel(in));
        setUserInfo(ParcelUtils.readFromParcel(in, io.rong.imlib.model.UserInfo.class));
    }

    public FriendMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            setMessage(jsonObj.getString("message"));
            if(jsonObj.has("user")){
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("message", message);
            if(getJSONUserInfo() != null)
                jsonObj.putOpt("user",getJSONUserInfo());

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Creator<FriendMessage> CREATOR = new Creator<FriendMessage>() {

        @Override
        public FriendMessage createFromParcel(Parcel source) {
            return new FriendMessage(source);
        }

        @Override
        public FriendMessage[] newArray(int size) {
            return new FriendMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, message);
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
