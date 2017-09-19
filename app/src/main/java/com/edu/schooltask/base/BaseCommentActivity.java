package com.edu.schooltask.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.view.CountView;
import com.edu.schooltask.view.MyScrollView;
import com.edu.schooltask.view.listener.ItemChildClickListener;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.listener.ItemLongClickListener;
import com.edu.schooltask.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.view.recyclerview.CommentRecyclerView;
import com.edu.schooltask.utils.GsonUtil;
import com.orhanobut.dialogplus.DialogPlus;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/9/17.
 */

public abstract class BaseCommentActivity extends BaseActivity implements MyScrollView.OnScrollListener{
    public final static int COMMENT_ORDER = 0;
    public final static int COMMENT_DYNAMIC = 1;

    protected @BindView(R.id.comment_msv) MyScrollView scrollView;
    protected @BindView(R.id.comment_prl) PullRefreshLayout refreshLayout;
    protected @BindView(R.id.comment_crv) CommentRecyclerView commentRecyclerView;
    protected @BindView(R.id.comment_header) LinearLayout headerLayout;
    protected @BindView(R.id.comment_count_layout) RelativeLayout countLayout;
    protected @BindView(R.id.comment_count_layout_top) RelativeLayout countLayoutTop;

    @OnClick(R.id.comment_btn)
    public void commentBtn(){
        comment();
    }

    private Result getCommentListResult = new Result() {
        @Override
        public void onResponse(int id) {
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(int id, Object data) {
            BaseList<Comment> commentList = GsonUtil.toCommentList(data);
            commentRecyclerView.add(commentList.getList(), commentList.isMore());
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    protected CountView countView;

    @Override
    public void init() {
        scrollView.setOnScrollListener(this);
        countView = new CountView(this, null);
        countView.setItems("按时间倒序", "按时间正序", "按回复数");
        countView.setOnOrderChangedListener(new CountView.OnOrderChangedListener() {
            @Override
            public void onOrderChanged(String order) {
                onScroll(0);
                commentRecyclerView.scrollToPosition(0);
                commentRecyclerView.refresh();
            }
        });
        int[] count = defaultCount();
        countView.setCount(count[0], count[1]);
        countLayout.addView(countView);

        commentRecyclerView.setOnRequestPageDataListener(new BasePageRecyclerView.OnRequestPageDataListener() {
            @Override
            public void onRequestPageData(int page) {
                getCommentList(getCommentListResult, page);
            }
        });
        commentRecyclerView.setItemClickListener(new ItemClickListener<Comment>() {
            @Override
            public void onItemClick(int position, Comment comment) {
                onCommentClick(comment);
            }
        });
        commentRecyclerView.setItemChildClickListener(new ItemChildClickListener<Comment>() {
            @Override
            public void onItemChildClick(View view, int position, Comment comment) {
                openReply(comment);
            }
        });
        commentRecyclerView.setItemLongClickListener(new ItemLongClickListener<Comment>() {
            @Override
            public void onItemLongClick(int position, Comment comment) {
                onLongClick(comment);
            }
        });
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refreshLayout.setRefreshing(true);
        refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case COMMENT_REQUEST:   //评论成功刷新评论
                commentRecyclerView.refresh();
                countView.addCommentCount();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScroll(int scrollY) {
        if(scrollY >= headerLayout.getHeight()){
            if (countView.getParent() != countLayoutTop) {
                countLayout.removeView(countView);
                countLayoutTop.addView(countView);
                countLayoutTop.setVisibility(View.VISIBLE);
            }
        }else{
            if (countView.getParent() != countLayout) {
                countLayoutTop.removeView(countView);
                countLayout.addView(countView);
                countLayoutTop.setVisibility(View.INVISIBLE);
            }
        }
        if(scrollY > 0) refreshLayout.setEnabled(false);
        else refreshLayout.setEnabled(true);
    }

    public abstract int[] defaultCount();
    public abstract void comment();
    public abstract void getCommentList(Result result, int page);
    public abstract void onCommentClick(Comment comment);
    public abstract void openReply(Comment comment);
    public abstract void refresh();

    public void onLongClick(final Comment comment){
        String title = comment.getUserInfo().getName() + ":" + comment.getComment();
        final boolean isParent = comment.getParentId() == 0;
        String[] parentItems = new String[]{"复制评论","查看回复","回复Ta"};
        String[] childItems = new String[]{"复制评论","回复Ta"};
        DialogUtil.createListDialog(this, title, new DialogUtil.ListItemClickListener() {
            @Override
            public void onItemClick(int position, String item) {
                switch (position){
                    case 0:
                        StringUtil.copy(BaseCommentActivity.this, comment.getComment());
                        toastShort("已经复制到粘贴板");
                        break;
                    case 1:
                        if (isParent) openReply(comment);
                        else onCommentClick(comment);
                        break;
                    case 2:
                        if (isParent) onCommentClick(comment);
                        break;
                }
            }
        }, isParent ? parentItems : childItems).show();
    }
}
