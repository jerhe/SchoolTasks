package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.item.MessageItem;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class MessageAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {
    public MessageAdapter(int layoutResId, List<MessageItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItem item) {
        UserBaseInfo user = item.getUser();
        CircleImageView headView = helper.getView(R.id.message_head);
        GlideUtil.setHead(headView.getContext(), user.getUserId(), headView, false);
        helper.setText(R.id.message_name, user.getName());

        Poll lastPoll = item.getLastPoll();
        if(lastPoll.getImage().length() == 0)
            helper.setText(R.id.message_msg, lastPoll.getMsg());
        else
            helper.setText(R.id.message_msg, "[图片]");

        int pollSize = 0;   //未读数
        for(Poll poll : item.getPolls()){
            if(!poll.isHasRead()) pollSize++;
        }
        if(pollSize != 0) {
            helper.setVisible(R.id.message_time, false);
            helper.setVisible(R.id.message_count, true);
            helper.setText(R.id.message_count, pollSize + "");
        }
        else{
            helper.setVisible(R.id.message_count, false);
            helper.setVisible(R.id.message_time, true);
            helper.setText(R.id.message_time, DateUtil.getMessageLong(item.getLastTime()));
        }
    }
}
