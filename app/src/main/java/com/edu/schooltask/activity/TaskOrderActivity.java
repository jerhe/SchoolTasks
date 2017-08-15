package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.StateAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.beans.TaskCommentList;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.item.StateItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CommentRecyclerView;
import com.edu.schooltask.view.CommentReplyView;
import com.edu.schooltask.view.TaskItemView;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.task.comment.GetTaskChildCommentEvent;
import server.api.task.comment.GetTaskCommentEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderInfoEvent;


//intent param: order_id
public class TaskOrderActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.order_layout) ScrollView orderLayout;
    @BindView(R.id.order_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.order_id) TextView orderIdText;
    @BindView(R.id.order_tiv) TaskItemView taskItemView;
    @BindView(R.id.order_confirm_btn) Button confirmBtn;
    @BindView(R.id.order_finish_btn) Button finishBtn;
    @BindView(R.id.order_overtime_btn) Button overtimeBtn;
    @BindView(R.id.order_cancel_btn) Button cancelBtn;
    @BindView(R.id.order_abandon_btn) Button abandonBtn;
    @BindView(R.id.order_state_rv) RecyclerView stateRecyclerView;
    @BindView(R.id.order_crv) CommentRecyclerView commentRecyclerView;
    @BindView(R.id.order_comment_reply_view) CommentReplyView commentReplyView;

    private StateAdapter stateAdapter;
    private List<StateItem> stateList = new ArrayList<>();

    private String orderId;

    UserInfo me;
    UserInfoBase releaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
        initView();
        getTaskOrderInfo();
    }

    private void initView(){
        me = UserUtil.getLoginUser();
        stateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stateRecyclerView.setNestedScrollingEnabled(false);
        stateAdapter = new StateAdapter(R.layout.item_order_state, stateList);
        stateAdapter.openLoadAnimation();
        stateRecyclerView.setAdapter(stateAdapter);
        stateAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UserInfoBase userInfoBase = stateList.get(position).getAcceptUser();
                switch (view.getId()){
                    case R.id.os_user_layout:
                        Intent userIntent = new Intent(TaskOrderActivity.this, UserActivity.class);
                        userIntent.putExtra("user", userInfoBase);
                        startActivity(userIntent);
                        break;
                    case R.id.os_user_talk:
                        if(!me.getUserId().equals(userInfoBase.getUserId())){
                            Intent talkIntent = new Intent(TaskOrderActivity.this, PrivateMessageActivity.class);
                            talkIntent.putExtra("user", userInfoBase);
                            startActivity(talkIntent);
                        }
                        break;
                }
            }
        });

        commentRecyclerView.setNestedScrollingEnabled(false);
        commentRecyclerView.setGetCommentListener(new CommentRecyclerView.GetCommentListener() {
            @Override
            public void getComment(int page) {
                SchoolTask.getTaskComment(orderId, page);
            }
        });
        commentRecyclerView.initChild(commentReplyView,
                new CommentRecyclerView.GetCommentListener() {
                    @Override
                    public void getComment(int page) {
                        SchoolTask.getTaskChildComment(orderId, commentRecyclerView.getParentId(), page);
                    }
                }, null, null);
        commentRecyclerView.setOppositeView(toolbar);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentRecyclerView.refresh();
            }
        });
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
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrderInfo(GetTaskOrderInfoEvent event){
        if(event.isOk()){
            commentRecyclerView.getComment();
            abandonBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.GONE);
            overtimeBtn.setVisibility(View.GONE);
            TaskOrder taskOrder = GsonUtil.toTaskOrder(event.getData());
            releaseUser = taskOrder.getReleaseUser();
            boolean isReleaseUser = UserUtil.getLoginUser().getUserId().equals(releaseUser.getUserId());
            orderIdText.setText(orderId);
            taskItemView.setAll(new TaskItem(taskOrder), true);

            //状态
            int state = taskOrder.getState();
            UserInfoBase acceptUser = taskOrder.getAcceptUser();
            boolean isMe = false;
            if(acceptUser != null) isMe = acceptUser.getUserId().equals(me.getUserId());
            stateList.add(new StateItem(true, "发布订单", taskOrder.getReleaseTime()));
            switch (state){
                case 0:
                    stateList.add(new StateItem(false, "等待接单", ""));
                    if(isReleaseUser){
                        cancelBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setOnClickListener(this);
                    }
                    break;
                case 1:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser, isMe));
                    stateList.add(new StateItem(false, "等待完成", ""));
                    if(isReleaseUser){
                        overtimeBtn.setVisibility(View.VISIBLE);
                        overtimeBtn.setOnClickListener(this);
                    }
                    else{
                        abandonBtn.setVisibility(View.VISIBLE);
                        abandonBtn.setOnClickListener(this);
                        finishBtn.setVisibility(View.VISIBLE);
                        finishBtn.setOnClickListener(this);
                    }
                    break;
                case 2:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser, isMe));
                    stateList.add(new StateItem(true, "任务完成", taskOrder.getFinishTime()));
                    stateList.add(new StateItem(true, "等待确认", "超过三天将自动确认"));
                    if(isReleaseUser) {
                        confirmBtn.setVisibility(View.VISIBLE);
                        confirmBtn.setOnClickListener(this);
                    }
                    break;
                case 3:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser, isMe));
                    stateList.add(new StateItem(true, "任务完成", taskOrder.getFinishTime()));
                    stateList.add(new StateItem(true, "订单完成", ""));
                    break;
                case 4:
                    stateList.add(new StateItem(true, "订单超时", taskOrder.getOverTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 5:
                    stateList.add(new StateItem(true, "订单取消", taskOrder.getCancelTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 6:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser, isMe));
                    stateList.add(new StateItem(true, "放弃任务", taskOrder.getAbandonTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 7:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser, isMe));
                    stateList.add(new StateItem(true, "任务超时", taskOrder.getOverTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
            }
            stateAdapter.notifyDataSetChanged();
            orderLayout.setVisibility(View.VISIBLE);
            orderLayout.startAnimation(AnimationUtils.loadAnimation(TaskOrderActivity.this, R.anim.fade_in));
        }
        else{
            stateList.clear();
            toastShort(event.getError());
        }
    }

    private void getTaskOrderInfo(){
        stateList.clear();
        refreshLayout.setRefreshing(true);
        SchoolTask.getOrderInfo(orderId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeTaskOrderState(ChangeTaskOrderStateEvent event){
        if(event.isOk()){
            toastShort("成功");
            getTaskOrderInfo();
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
            if(orderId.equals(taskCommentList.getOrderId())){
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                commentRecyclerView.addData(taskComments);
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
            commentReplyView.addData(taskComments);
        }
        else{
            toastShort(event.getError());
        }
    }


    @Override
    public void onClick(View v) {
        DialogPlus confirmDialog = null;
        switch (v.getId()){
            case R.id.order_cancel_btn:
                confirmDialog = DialogUtil.createTextDialog(this, "提示",
                        "是否要取消该任务", "取消后订单的金额(不包括时限支出)将退回您的账户", "是",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                SchoolTask.changeTaskOrderState(orderId, 5);
                            }
                        }, "否");
                break;
            case R.id.order_overtime_btn:
                confirmDialog = DialogUtil.createTextDialog(this, "提示",
                        "任务已经超时？", "超时后订单的金额(不包括时限支出)将退回您的账户", "超时",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                SchoolTask.changeTaskOrderState(orderId, 7);
                            }
                        }, "否");
                break;
            case R.id.order_confirm_btn:
                confirmDialog = DialogUtil.createTextDialog(this, "提示",
                        "确定任务已经完成？", "确定完成后接单人将收到您的付款", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                SchoolTask.changeTaskOrderState(orderId, 3);
                            }
                        }, "取消");
                break;
            case R.id.order_finish_btn:
                confirmDialog = DialogUtil.createTextDialog(this, "提示",
                        "确定任务已经完成？", "完成任务时请尽量保留照片或者其余可以表明您已经完成任务的物件，以避免不必要的纠纷", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                SchoolTask.changeTaskOrderState(orderId, 2);
                            }
                        }, "取消");
                break;
            case R.id.order_abandon_btn:
                confirmDialog = DialogUtil.createTextDialog(this, "提示",
                        "确定要放弃任务吗？", "放弃任务将会导致您的信用值降低，可信用值过低会造成您不能接受任务", "放弃",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                SchoolTask.changeTaskOrderState(orderId, 6);
                            }
                        }, "取消");
                break;
        }
        if( confirmDialog != null) confirmDialog.show();
    }
}
