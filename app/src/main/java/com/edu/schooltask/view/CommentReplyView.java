package com.edu.schooltask.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.TaskComment;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/15.
 */

public class CommentReplyView extends LinearLayout {
    private Toolbar toolbar;
    private TextView titleText;
    private PullRefreshLayout refreshLayout;
    private CommentRecyclerView commentRecyclerView;

    View oppositeView;

    public CommentReplyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_comment_reply, this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleText = (TextView) findViewById(R.id.toolbar_title);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.cp_prl);
        commentRecyclerView = (CommentRecyclerView) findViewById(R.id.cp_rv);

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
            }
        });
        titleText.setText("查看回复");
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentRecyclerView.refresh();
            }
        });
    }

    public void setRefresh(boolean refreshing){
        refreshLayout.setRefreshing(refreshing);
    }

    public void setOppositeView(View view){
        oppositeView = view;
    }

    //以下皆为调用commentRecyclerView接口

    public void setGetCommentListener(CommentRecyclerView.GetCommentListener l){
        commentRecyclerView.setGetCommentListener(l);
    }

    public void setOnItemClickListener(BaseQuickAdapter.OnItemClickListener l){
        commentRecyclerView.setOnItemClickListener(l);
    }

    public void refresh(){
        commentRecyclerView.refresh();
    }

    public void addData(List<TaskComment> comments){
        commentRecyclerView.addData(comments);
    }

    public List<TaskComment> getData(){
        return commentRecyclerView.getData();
    }


}