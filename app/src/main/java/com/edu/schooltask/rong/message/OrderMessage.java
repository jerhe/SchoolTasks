package com.edu.schooltask.rong.message;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.push.common.ParcelUtils;

/**
 * 订单消息
 * Created by 夜夜通宵 on 2017/8/25.
 */

@MessageTag(value = "ST:Order",  flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class OrderMessage extends MessageContent{
    private String orderId; //订单号
    private String content; //订单内容
    private String message; //消息内容

    public OrderMessage(String orderId, String content, String message) {
        this.orderId = orderId;
        this.content = content;
        this.message = message;
    }

    public OrderMessage(Parcel in) {
        setOrderId(ParcelUtils.readFromParcel(in));
        setContent(ParcelUtils.readFromParcel(in));
        setMessage(ParcelUtils.readFromParcel(in));
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    }

    public OrderMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            setOrderId(jsonObj.getString("orderId"));
            setContent(jsonObj.getString("content"));
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
            jsonObj.put("orderId", orderId);
            jsonObj.put("content", content);
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

    public static final Creator<OrderMessage> CREATOR = new Creator<OrderMessage>() {

        @Override
        public OrderMessage createFromParcel(Parcel source) {
            return new OrderMessage(source);
        }

        @Override
        public OrderMessage[] newArray(int size) {
            return new OrderMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, orderId);
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, message);
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
