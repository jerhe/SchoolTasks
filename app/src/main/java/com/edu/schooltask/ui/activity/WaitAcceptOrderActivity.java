package com.edu.schooltask.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.beans.comment.TaskCommentList;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.ui.view.PayPasswordView;
import com.edu.schooltask.ui.view.PayView;
import com.edu.schooltask.ui.view.recyclerview.TextRecyclerView;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.EncriptUtil;
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
import server.api.event.task.AcceptTaskEvent;
import server.api.event.task.comment.GetTaskCommentListEvent;
import server.api.event.task.comment.GetTaskReplyListEvent;
import server.api.event.task.comment.NewTaskCommentEvent;
import server.api.event.task.GetTaskInfoEvent;

public class WaitAcceptOrderActivity extends BaseActivity {
    @BindView(R.id.wao_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.wao_crv) CommentRecyclerView commentRecyclerView;
    @BindView(R.id.wao_comment_reply_view) CommentReplyView commentReplyView;
    @BindView(R.id.wao_accept_btn) TextView acceptBtn;
    @BindView(R.id.wao_comment_btn) ImageView commentBtn;
    @BindView(R.id.wao_btn_layout) RelativeLayout btnLayout;
    @BindView(R.id.wao_cib) CommentInputBoard inputBoard;
    @BindView(R.id.wao_pv) PayView payView;

    @OnClick(R.id.wao_accept_btn)
    public void accept(){
        if(taskItem == null) return;
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user == null) {
            toastShort(getString(R.string.unlogin_tip));
            openActivity(LoginActivity.class);
            return;
        }
        if(user.getUserId().equals(taskItem.getUserInfo().getUserId())){
            toastShort(getString(R.string.acceptSelfTip));
            return;
        }
        payView.setTitle("接单确认");
        payView.show();
    }
    @OnClick(R.id.wao_comment_btn)
    public void comment(){
        inputBoard.show();
    }

    //header
    TaskItemView taskItemView;
    TaskCountView taskCountView;

    ProgressDialog progressDialog;

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
                    toastShort(getString(R.string.commentInputTip));
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

        payView.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(WaitAcceptOrderActivity.this, "", "接单中...", true, false);
                SchoolTask.acceptTask(orderId, EncriptUtil.getMD5(password));
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
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
        if(progressDialog != null) progressDialog.dismiss();
        if(event.isOk()){
            toastShort(getString(R.string.acceptTip));
            payView.hide();
            acceptBtn.setText(getString(R.string.hasAccept));
            openTaskOrderActivity(orderId);
            finish();
        }
        else{
            payView.clearPassword();
            toastShort(event.getError());
            switch (event.getCode()){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 304:
                    break;
                default:
                    payView.hide();
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewTaskComment(NewTaskCommentEvent event){
        if(event.isOk()){
            toastShort(getString(R.string.commentSuccess));
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
