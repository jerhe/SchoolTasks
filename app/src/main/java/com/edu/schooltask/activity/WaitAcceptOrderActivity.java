package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskCommentList;
import com.edu.schooltask.beans.TaskCount;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CommentInputBoard;
import com.edu.schooltask.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.view.recyclerview.CommentRecyclerView;
import com.edu.schooltask.view.CommentReplyView;
import com.edu.schooltask.view.TaskCountView;
import com.edu.schooltask.view.TaskItemView;
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
import server.api.task.comment.GetTaskChildCommentEvent;
import server.api.task.comment.GetTaskCommentEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.count.GetTaskCountEvent;

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
            UserInfo user = UserUtil.getLoginUser();
            if(user != null){
                if(user.getUserId().equals(taskItem.getUser().getUserId())){
                    toastShort("不能接自己发布的任务哦");
                }
                else{
                    DialogUtil.createTextDialog(WaitAcceptOrderActivity.this, "提示", "确定接受该任务吗",
                            "请确保自身有能力完成该任务，接单后对于订单的疑问可联系发布人", "确定", new DialogUtil.OnClickListener() {
                                @Override
                                public void onClick(DialogPlus dialogPlus) {
                                    SchoolTask.acceptTask(taskItem.getOrderId());
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
    TaskCountView taskCountView;

    TaskItem taskItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_accept_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        //初始化任务信息和评论数和浏览数
        Intent intent = getIntent();
        taskItem = (TaskItem) intent.getSerializableExtra("taskItem");

        //初始化评论框
        inputBoard.setOppositeView(btnLayout);
        inputBoard.setOnBtnClickListener(new CommentInputBoard.OnBtnClickListener() {
            @Override
            public void btnClick(String comment) {
                if(comment.length() != 0){
                    SchoolTask.comment(taskItem.getOrderId(), commentRecyclerView.getParentId(),
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
                commentRecyclerView.refresh();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView(){
        commentRecyclerView.setOnGetPageDataListener(new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                if(page == 0) SchoolTask.getTaskCount(taskItem.getOrderId());
                SchoolTask.getTaskComment(taskItem.getOrderId(), page);
            }
        });
        commentRecyclerView.initChild(commentReplyView, new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                SchoolTask.getTaskChildComment(taskItem.getOrderId(), commentRecyclerView.getParentId(), page);
            }
        }, inputBoard);
        commentRecyclerView.setOppositeView(toolbar);
        commentRecyclerView.refresh();

        //添加头部
        TaskItemView taskItemView = new TaskItemView(this, null);
        taskItemView.setAll(taskItem, true);
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
        if(commentReplyView.isShown()){
            commentRecyclerView.hideChild();
            return;
        }
        if(inputBoard.isShown()){
            inputBoard.clearHint();
            commentRecyclerView.setParentId(0);
            commentRecyclerView.setToUserId("");
            inputBoard.hide();
        }
        else{
            finish();
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
            commentRecyclerView.refresh();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskComment(GetTaskCommentEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            if(taskCommentList.getOrderId().equals(taskItem.getOrderId())){
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                commentRecyclerView.add(taskComments);
            }
        }
        else{
            toastShort(event.getError());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskChildComment(GetTaskChildCommentEvent event){
        commentReplyView.setRefresh(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            List<TaskComment> taskComments = taskCommentList.getTaskComments();
            commentReplyView.addComments(taskComments);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskCount(GetTaskCountEvent event){
        if(event.isOk()){
            TaskCount taskCount = GsonUtil.toTaskCount(event.getData());
            taskCountView.setCount(taskCount);
        }
    }
}
