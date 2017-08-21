package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.beans.comment.TaskCommentList;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.CommentInputBoard;
import com.edu.schooltask.ui.view.CommentReplyView;
import com.edu.schooltask.ui.view.TaskCountView;
import com.edu.schooltask.ui.view.TaskItemView;
import com.edu.schooltask.ui.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.CommentRecyclerView;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.task.accept.AcceptTaskEvent;
import server.api.task.comment.GetTaskCommentListEvent;
import server.api.task.comment.GetTaskReplyListEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.get.GetTaskInfoEvent;

public class WaitAcceptOrderActivity extends BaseActivity {
    @BindView(R.id.wao_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.wao_crv) CommentRecyclerView commentRecyclerView;
    @BindView(R.id.wao_comment_reply_view) CommentReplyView commentReplyView;
    @BindView(R.id.wao_accept_btn) Button acceptBtn;
    @BindView(R.id.wao_comment_btn) Button commentBtn;
    @BindView(R.id.wao_btn_layout) RelativeLayout btnLayout;
    @BindView(R.id.wao_cib) CommentInputBoard inputBoard;

    @OnClick(R.id.wao_accept_btn)
    public void accept(){
        if("接单".equals(acceptBtn.getText())){
            UserInfoWithToken user = UserUtil.getLoginUser();
            if(user != null){
                if(user.getUserId().equals(taskItem.getUserInfo().getUserId())){
                    toastShort("不能接自己发布的任务哦");
                }
                else{
                    DialogUtil.createTextDialog(WaitAcceptOrderActivity.this, "提示", "确定接受该任务吗",
                            "请确保自身有能力完成该任务，接单后对于订单的疑问可联系发布人", "确定", new DialogUtil.OnClickListener() {
                                @Override
                                public void onClick(DialogPlus dialogPlus) {
                                    SchoolTask.acceptTask(orderId);
                                }
                            }, "取消").show();
                }
            }
            else{
                toastShort("请先登录");
                openActivity(LoginActivity.class);
            }
        }
    }
    @OnClick(R.id.wao_comment_btn)
    public void comment(){
        inputBoard.show();
    }

    //header
    TaskItemView taskItemView;
    TaskCountView taskCountView;

    TaskItem taskItem;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_accept_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        //初始化任务信息和评论数和浏览数
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        //初始化评论框
        inputBoard.setOppositeView(btnLayout);
        inputBoard.setOnBtnClickListener(new CommentInputBoard.OnBtnClickListener() {
            @Override
            public void btnClick(String comment) {
                if(comment.length() != 0){
                    SchoolTask.comment(orderId, commentRecyclerView.getParentId(),
                            commentRecyclerView.getToUserId(), comment);
                }
                else{
                    toastShort("请输入评论");
                }
            }
        });

        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SchoolTask.getTaskInfo(orderId);
                commentRecyclerView.refresh();
            }
        });
        initRecyclerView();

        SchoolTask.getTaskInfo(orderId);
    }

    private void initRecyclerView(){
        commentRecyclerView.setOnGetPageDataListener(new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                SchoolTask.getTaskComment(orderId, page);
            }
        });
        commentRecyclerView.initChild(commentReplyView, new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                SchoolTask.getTaskReplyList(orderId, commentRecyclerView.getParentId(), page);
            }
        }, inputBoard);
        commentRecyclerView.setOppositeView(toolbar);
        commentRecyclerView.refresh();

        //添加头部
        taskItemView = new TaskItemView(this, null);
        commentRecyclerView.addHeader(taskItemView);

        taskCountView = new TaskCountView(this, null);
        commentRecyclerView.addHeader(taskCountView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(inputBoard.isShown()){
            inputBoard.clearHint();
            if(!commentReplyView.isShown()) commentRecyclerView.setParentId(0);
            commentRecyclerView.setToUserId("");
            inputBoard.hide();
            return;
        }
        if(commentReplyView.isShown()){
            commentRecyclerView.hideChild();
            return;
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskInfo(GetTaskInfoEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            taskItem = GsonUtil.toTaskItem(event.getData());
            taskItemView.setAll(taskItem, true);
            taskCountView.setCount(taskItem.getCommentCount(), taskItem.getLookCount());
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAcceptTask(AcceptTaskEvent event){
        if(event.isOk()){
            toastShort("接受任务成功，快去完成吧");
            acceptBtn.setText("已接单");
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewTaskComment(NewTaskCommentEvent event){
        if(event.isOk()){
            toastShort("评论成功");
            inputBoard.clear();
            SchoolTask.getTaskInfo(orderId);
            commentRecyclerView.refresh();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCommentList(GetTaskCommentListEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            if(taskCommentList.getOrderId().equals(orderId)){
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                commentRecyclerView.add(taskComments);
            }
        }
        else{
            toastShort(event.getError());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetReplyList(GetTaskReplyListEvent event){
        commentReplyView.setRefresh(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            List<TaskComment> taskComments = taskCommentList.getTaskComments();
            //设置回复对象昵称
            for(TaskComment taskComment : taskComments){
                String toId = taskComment.getToId();
                commentReplyView.addUserMap(taskComment.getFromId(), taskComment.getUserInfo().getName());
                if(!StringUtil.isEmpty(toId)){
                    taskComment.setToName(commentReplyView.getUserName(toId));
                }
            }
            commentReplyView.addComments(taskComments);
        }
        else{
            toastShort(event.getError());
        }
    }
}
