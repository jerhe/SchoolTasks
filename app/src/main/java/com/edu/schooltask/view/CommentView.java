package com.edu.schooltask.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.NineAdapter;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.view.useritem.UserItemCommentView;
import com.edu.schooltask.utils.StringUtil;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/9.
 */

public class CommentView extends LinearLayout {

    private UserItemCommentView userView;
    private TextView commentText;
    private TextView childCountText;
    private NineGridView nineGridView;

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_comment, this);
        userView = (UserItemCommentView) findViewById(R.id.comment_uicv);
        commentText = (TextView) findViewById(R.id.comment_text);
        //commentText.setMovementMethod(LinkMovementMethod.getInstance());   //设置可点击
        //commentText.setFocusable(false);   //点击冲突
        childCountText = (TextView) findViewById(R.id.comment_reply_count);
        nineGridView = (NineGridView) findViewById(R.id.comment_image);
    }

    public void setAll(BaseViewHolder helper, Comment comment, boolean isChildView){
        //用户信息
        UserInfo userInfo = comment.getUserInfo();
        userView.setAll(userInfo, comment.getCreateTime(), userInfo.getSchool());
        //评论信息
        if(comment.getParentId() != 0 || isChildView){ //子评论
            commentText.setVisibility(VISIBLE);
            SpannableString text;
            String toName = comment.getToName();
            if(StringUtil.isEmpty(toName)){
                text = StringUtil.atString(getContext(), comment.getComment());
            }
            else{
                String replyText = "回复@" + toName +" ："+comment.getComment();
                text = StringUtil.atString(getContext(), replyText);
            }
            if(text.length() == 0) commentText.setVisibility(GONE);
            else commentText.setText(text);
            childCountText.setVisibility(GONE);
        }
        else{   //顶层评论
            SpannableString text = StringUtil.atString(getContext(), comment.getComment());
            if(text.length() == 0) commentText.setVisibility(GONE);
            else{
                commentText.setVisibility(VISIBLE);
                commentText.setText(text);
                if(comment.getReplyCount() != 0){
                    childCountText.setVisibility(VISIBLE);
                    childCountText.setText(comment.getReplyCount() + "条回复");
                    if(helper != null) helper.addOnClickListener(R.id.comment_reply_count);
                }
                else{
                    childCountText.setVisibility(GONE);
                }
            }
        }
        //图片
        String image = comment.getImage();
        if(StringUtil.isEmpty(image)) {
            nineGridView.setVisibility(GONE);
        }
        else{
            nineGridView.setVisibility(VISIBLE);
            List<ImageInfo> imageInfos = new ArrayList<>();
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(image);
            imageInfo.setBigImageUrl(image);
            imageInfos.add(imageInfo);
            NineAdapter nineAdapter = new NineAdapter(getContext(), imageInfos);
            nineGridView.setAdapter(nineAdapter);
        }
    }
}
