package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.view.DynamicView;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import server.api.client.DynamicClient;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class DynamicAdapter extends BaseQuickAdapter<Dynamic, BaseViewHolder> {

    public DynamicAdapter(@Nullable List<Dynamic> data) {
        super(R.layout.item_dynamic, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final Dynamic item) {
        DynamicView dynamicView = helper.getView(R.id.dynamic_dv);
        dynamicView.setAll(item, false);
        final ImageTextView likeCountView = helper.getView(R.id.dynamic_like_count);
        likeCountView.setText(String.valueOf(item.getLikeCount()));
        setLike(likeCountView, item.isLike(), false);
        likeCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserUtil.hasLogin()) {
                    EventBus.getDefault().post(new UnloginEvent());
                    return;
                }
                item.setLike(!item.isLike());
                setLike(likeCountView, item.isLike(), true);
                item.setLikeCount(item.getLikeCount() + (item.isLike() ? 1 : -1));
                likeCountView.setText(String.valueOf(item.getLikeCount()));
                DynamicClient.dynamicLike(new Result(true) {
                    @Override
                    public void onResponse(int id) {

                    }

                    @Override
                    public void onSuccess(int id, Object data) {

                    }

                    @Override
                    public void onFailed(int id, int code, String error) {

                    }
                }, item.getId(), item.isLike());
            }
        });
        ImageTextView commentCountView = helper.getView(R.id.dynamic_comment_count);
        commentCountView.setText(String.valueOf(item.getCommentCount()));
        helper.setText(R.id.dynamic_comment_long, DateUtil.getLong(DateUtil.stringToCalendar(item.getCommentTime())));
    }

    public void setLike(ImageTextView likeCountView, boolean isLike, boolean animate){
        if(isLike){
            if(animate) {
                Animation likeAnimation = AnimationUtils.loadAnimation(likeCountView.getContext(), R.anim.like);
                likeAnimation.setInterpolator(new OvershootInterpolator());
                likeCountView.startIconAnimation(likeAnimation);
            }
            likeCountView.setIcon(R.drawable.ic_like_light);
        }
        else{
            likeCountView.setIcon(R.drawable.ic_like);
        }
    }
}
