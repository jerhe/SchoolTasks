package com.edu.schooltask.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.PrivateMessage;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/5/29.
 */

public class PrivateMessageAdapter extends BaseMultiItemQuickAdapter<PrivateMessage, BaseViewHolder> {
    int halfScreenWidth;
    public PrivateMessageAdapter(List<PrivateMessage> data, int halfScreenWidth) {
        super(data);
        this.halfScreenWidth = halfScreenWidth;
        addItemType(PrivateMessage.SYSTEM, R.layout.item_private_message_system);
        addItemType(PrivateMessage.RECEIVE, R.layout.item_private_message_receive);
        addItemType(PrivateMessage.SEND, R.layout.item_private_message_send);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMessage item) {
        Poll poll = item.getPoll();
        switch (item.getItemType()){
            case 0: //通知
                helper.setText(R.id.pms_time, DateUtil.getMessageLong(poll.getCreateTime()));
                helper.setText(R.id.pms_title, "订单消息");
                helper.setText(R.id.pms_content, poll.getContent()+ "\n订单号："
                        + poll.getOrderId() + "\n点击查看订单");
                break;
            case 1: //接收
            case 2: //发送
                CircleImageView fromHeadView = helper.getView(R.id.pm_head);
                GlideUtil.setHead(fromHeadView.getContext(), poll.getFromUser().getUserId(),
                        fromHeadView, false);
                if(item.isShowTime()){
                    helper.setVisible(R.id.pm_time,true);
                    helper.setText(R.id.pm_time, DateUtil.getMessageLong(poll.getCreateTime()));
                }
                else{
                    helper.setVisible(R.id.pm_time,false);
                }
                if(poll.getImage().length() != 0){
                    helper.setVisible(R.id.pm_image,true);
                    helper.setVisible(R.id.pm_text,false);
                    ImageView imageView = helper.getView(R.id.pm_image);
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.width = halfScreenWidth;
                    layoutParams.height = poll.getHeight() * halfScreenWidth / poll.getWidth();
                    Glide.with(imageView.getContext())
                            .load(poll.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                    helper.addOnClickListener(R.id.pm_image);
                }
                else{
                    helper.setVisible(R.id.pm_image,false);
                    helper.setVisible(R.id.pm_text,true);
                    helper.setText(R.id.pm_text, poll.getContent());
                }
                helper.addOnClickListener(R.id.pm_head);
                break;

        }
    }
}
