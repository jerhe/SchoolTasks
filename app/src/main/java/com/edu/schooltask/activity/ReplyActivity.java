package com.edu.schooltask.activity;

import android.content.Intent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.CommentAdapter;
import com.edu.schooltask.base.BaseCommentActivity;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.view.CountView;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.DynamicClient;
import server.api.client.TaskClient;
import server.api.result.Result;

import static com.edu.schooltask.base.BaseCommentActivity.COMMENT_DYNAMIC;
import static com.edu.schooltask.base.BaseCommentActivity.COMMENT_ORDER;

public class ReplyActivity extends BaseActivity {

    @BindView(R.id.reply_cv) CountView countView;

    @OnClick(R.id.reply_reply)
    public void reply(){
        openCommentActivity(name, true);
    }

    private RefreshPageRecyclerView<Comment> recyclerView;
    private int commentType;
    private int commentId;
    private String orderId;
    private int dynamicId;
    private String name;

    @Override
    public int getLayout() {
        return R.layout.activity_reply;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        commentType = intent.getIntExtra("commentType", COMMENT_ORDER);
        commentId = intent.getIntExtra("commentId", 0);
        orderId = intent.getStringExtra("orderId");
        dynamicId = intent.getIntExtra("dynamicId", 0);
        name = intent.getStringExtra("name");
        int replyCount = intent.getIntExtra("replyCount", 0);

        countView.setItems("按时间倒序", "按时间正序");
        countView.hideLookCount();
        countView.setCommentHint("回复");
        countView.setCommentCount(replyCount);
        countView.setOnOrderChangedListener(new CountView.OnOrderChangedListener() {
            @Override
            public void onOrderChanged(String order) {
                recyclerView.refresh();
            }
        });

        recyclerView = new RefreshPageRecyclerView<Comment>(this, false) {
            @Override
            protected BaseQuickAdapter initAdapter(List<Comment> list) {
                return new CommentAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                if(commentType == COMMENT_ORDER)
                    TaskClient.getReplyList(result, commentId, countView.getOrder(),page);
                if(commentType == COMMENT_DYNAMIC)
                    DynamicClient.getReplyList(result, commentId, countView.getOrder(), page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<Comment> list = GsonUtil.toCommentList(data);
                countView.setCommentCount(list.getList().size());
                recyclerView.add(list.getList(), list.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, Comment comment) {
                openCommentActivity(comment.getUserInfo().getName(), false);
            }

            @Override
            protected void onItemLongClick(int position, Comment comment) {
                onLongClick(comment);
            }
        };
        addView(recyclerView);
        recyclerView.refresh();
    }

    private void openCommentActivity(String name, boolean isParent){
        if(commentType == COMMENT_ORDER)
            openOrderCommentActivity(orderId, commentId, name, isParent);
        if(commentType == COMMENT_DYNAMIC)
            openDynamicCommentActivity(dynamicId, commentId, name, isParent);
    }

    public void onLongClick(final Comment comment){
        String title = comment.getUserInfo().getName() + ":" + comment.getComment();
        String[] items = new String[]{"复制评论","回复Ta"};
        DialogUtil.createListDialog(this, title, new DialogUtil.ListItemClickListener() {
            @Override
            public void onItemClick(int position, String item) {
                switch (position){
                    case 0:
                        StringUtil.copy(ReplyActivity.this, comment.getComment());
                        toastShort("已经复制到粘贴板");
                        break;
                    case 1:
                        openCommentActivity(comment.getUserInfo().getName(), false);
                        break;
                }
            }
        }, items).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case COMMENT_REQUEST:   //评论成功刷新评论
                recyclerView.refresh();
                countView.addCommentCount();
                break;
            default:
                break;
        }
    }
}
