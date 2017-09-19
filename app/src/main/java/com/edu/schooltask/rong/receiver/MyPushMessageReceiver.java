package com.edu.schooltask.rong.receiver;

import android.content.Context;
import android.content.Intent;

import com.edu.schooltask.activity.FriendRequestActivity;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class MyPushMessageReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        String targetId = pushNotificationMessage.getTargetId();
        if(targetId.equals("验证消息")){
            Intent intent = new Intent(context, FriendRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
