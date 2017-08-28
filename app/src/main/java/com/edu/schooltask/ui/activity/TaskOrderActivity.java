package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.beans.comment.TaskCommentList;
import com.edu.schooltask.beans.task.TaskOrderInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.item.OrderStateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.CommentInputBoard;
import com.edu.schooltask.ui.view.recyclerview.CommentRecyclerView;
import com.edu.schooltask.ui.view.CommentReplyView;
import com.edu.schooltask.ui.view.recyclerview.OrderStateRecyclerView;
import com.edu.schooltask.ui.view.TaskCountView;
import com.edu.schooltask.ui.view.TaskItemView;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.task.comment.GetTaskReplyListEvent;
import server.api.task.comment.GetTaskCommentListEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderInfoEvent;


//intent param: orderId
public class TaskOrderActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.order_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.order_id) TextView orderIdText;
    @BindView(R.id.order_tiv) TaskItemView taskItemView;
    @BindView(R.id.order_bottom) RelativeLayout bottomLayout;
    @BindView(R.id.order_btn_layout) RelativeLayout btnLayout;
    @BindView(R.id.order_comment_btn) Button commentBtn;
    @BindView(R.id.order_confirm_btn) Button confirmBtn;
    @BindView(R.id.order_finish_btn) Button finishBtn;
    @BindView(R.id.order_overtime_btn) Button overtimeBtn;
    @BindView(R.id.order_cancel_btn) Button cancelBtn;
    @BindView(R.id.order_abandon_btn) Button abandonBtn;
    @BindView(R.id.order_cib) CommentInputBoard commentInputBoard;
    @BindView(R.id.order_osrv) OrderStateRecyclerView orderStateRecyclerView;
    @BindView(R.id.order_crv) CommentRecyclerView commentRecyclerView;
    @BindView(R.id.order_count) TaskCountView taskCountView;
    @BindView(R.id.order_comment_reply_view) CommentReplyView commentReplyView;

    @OnClick(R.id.order_comment_btn)
    public void showInputBoard(){
        commentInputBoard.show();
    }

    private String orderId;

    UserInfoWithToken me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        initView();
        orderStateRecyclerView.refresh();
    }

    private void initView(){
        me = UserUtil.getLoginUser();
        //状态列表初始化
        orderStateRecyclerView.setNestedScrollingEnabled(false);
        orderStateRecyclerView.setOnGetDataListener(new BaseRecyclerView.OnGetDataListener() {
            @Override
            public void onGetData() {
                SchoolTask.getOrderInfo(orderId);
            }
        });
        //评论列表初始化
        commentRecyclerView.setNestedScrollingEnabled(false);
        commentRecyclerView.setOnGetPageDataListener(new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                SchoolTask.getTaskComment(orderId, page);
            }
        });
        commentRecyclerView.initChild(commentReplyView,
                new BasePageRecyclerView.OnGetPageDataListener() {
                    @Override
                    public void onGetPageData(int page) {
                        SchoolTask.getTaskReplyList(orderId, commentRecyclerView.getParentId(), page);
                    }
                }, commentInputBoard);
        commentRecyclerView.setOppositeView(toolbar);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderStateRecyclerView.refresh();
            }
        });
        //评论框初始化
        commentInputBoard.setOppositeView(btnLayout);
        commentInputBoard.setOnBtnClickListener(new CommentInputBoard.OnBtnClickListener() {
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
        if(commentInputBoard.isShown()) {
            commentInputBoard.clearHint();
            commentRecyclerView.setParentId(0);
            commentRecyclerView.setToUserId("");
            commentInputBoard.hide();
        }
        else finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrderInfo(GetTaskOrderInfoEvent event){
        orderStateRecyclerView.clear(false);
        if(event.isOk()){
            commentRecyclerView.refresh();  //先获取订单状态再获取评论
            abandonBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.GONE);
            overtimeBtn.setVisibility(View.GONE);
            TaskOrderInfo taskOrderInfo = GsonUtil.toTaskOrder(event.getData());
            boolean isReleaseUser = me.getUserId().equals(taskOrderInfo.getReleaseId());
            orderIdText.setText(orderId);
            taskItemView.setAll(taskOrderInfo.toTaskItem(), true);
            //评论数和浏览数
            taskCountView.setCommentCount(taskOrderInfo.getTaskInfo().getCommentCount());
            taskCountView.setLookCount(taskOrderInfo.getTaskInfo().getLookCount());
            //状态
            int state = taskOrderInfo.getState();
            //接单人信息
            UserInfo acceptUser = taskOrderInfo.getAcceptUserInfo();
            orderStateRecyclerView.add(new OrderStateItem(true, "发布订单", taskOrderInfo.getReleaseTime()));
            if(state < 3){
                bottomLayout.setVisibility(View.VISIBLE);
                bottomLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            }
            else {
                bottomLayout.setVisibility(View.GONE);
                commentRecyclerView.cancelInputBoard();  //防止点击评论弹出
            }
            switch (state){
                case 0:
                    orderStateRecyclerView.add(new OrderStateItem(false, "等待接单", ""));
                    if(isReleaseUser){
                        cancelBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setOnClickListener(this);
                    }
                    break;
                case 1:
                    orderStateRecyclerView.add(new OrderStateItem(true, "已经接单", taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(false, "等待完成", ""));
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
                    orderStateRecyclerView.add(new OrderStateItem(true, "已经接单", taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, "任务完成", taskOrderInfo.getFinishedTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "等待确认", "超过三天将自动确认"));
                    if(isReleaseUser) {
                        confirmBtn.setVisibility(View.VISIBLE);
                        confirmBtn.setOnClickListener(this);
                    }
                    break;
                case 3:
                    orderStateRecyclerView.add(new OrderStateItem(true, "已经接单", taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, "任务完成", taskOrderInfo.getFinishedTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单完成", ""));
                    break;
                case 4:
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单超时", taskOrderInfo.getOverTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单失效", ""));
                    break;
                case 5:
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单取消", taskOrderInfo.getCancelTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单失效", ""));
                    break;
                case 6:
                    orderStateRecyclerView.add(new OrderStateItem(true, "已经接单", taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, "放弃任务", taskOrderInfo.getAbandonTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单失效", ""));
                    break;
                case 7:
                    orderStateRecyclerView.add(new OrderStateItem(true, "已经接单", taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, "任务超时", taskOrderInfo.getOverTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, "订单失效", ""));
                    break;
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewTaskComment(NewTaskCommentEvent event){
        if(event.isOk()){
            toastShort("评论成功");
            commentInputBoard.clear();
            orderStateRecyclerView.refresh();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeTaskOrderState(ChangeTaskOrderStateEvent event){
        if(event.isOk()){
            toastShort("成功");
            orderStateRecyclerView.refresh();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskComment(GetTaskCommentListEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            if(orderId.equals(taskCommentList.getOrderId())){
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                commentRecyclerView.add(taskComments);
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskReplyList(GetTaskReplyListEvent event){
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
