package com.edu.schooltask.rong.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.schooltask.R;
import com.edu.schooltask.rong.message.FriendMessage;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

/**
 *  messageContent 　　	对应的消息类型 ( 如：TextMessage.class )。
    showPortrait	    设置是否显示头像，默认为 true。
    centerInHorizontal	消息内容是否横向居中，默认 false。
    hide	            是否隐藏消息， 默认 false。
    showProgress	    是否显示发送进度，默认 true。
    showSummaryWithName	是否在会话的内容体里显示发送者名字，默认 true。
 */
@ProviderTag(messageContent = FriendMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class FriendMessageProvider extends IContainerItemProvider.MessageProvider<FriendMessage> {

    class ViewHolder {
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_text_item, null);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }



    public void bindView(View v, int position, final FriendMessage content, final UIMessage uiMessage) {
        final ViewHolder holder = (ViewHolder) v.getTag();
    }


    @Override
    public Spannable getContentSummary(FriendMessage data) {
        return new SpannableString(data.getMessage());
    }

    @Override
    public void onItemClick(View view, int i, FriendMessage message, UIMessage uiMessage) {

    }
}
