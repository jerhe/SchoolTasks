package com.edu.schooltask.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.MyScrollView;
import com.edu.schooltask.ui.view.PayPasswordView;
import com.edu.schooltask.ui.view.PayView;
import com.edu.schooltask.ui.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.beans.comment.TaskCommentList;
import com.edu.schooltask.beans.task.TaskOrderInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.item.OrderStateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
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
import server.api.event.task.UpdateTaskInfoEvent;
import server.api.event.task.comment.GetTaskReplyListEvent;
import server.api.event.task.comment.GetTaskCommentListEvent;
import server.api.event.task.comment.NewTaskCommentEvent;
import server.api.event.task.order.ChangeTaskOrderStateEvent;
import server.api.event.task.order.GetTaskOrderInfoEvent;


//intent param: orderId
public class TaskOrderActivity extends BaseActivity implements View.OnClickListener, MyScrollView.OnScrollListener{
    @BindView(R.id.order_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.order_id) TextView orderIdText;
    @BindView(R.id.order_tiv) TaskItemView taskItemView;
    @BindView(R.id.order_bottom) RelativeLayout bottomLayout;
    @BindView(R.id.order_btn_layout) RelativeLayout btnLayout;
    @BindView(R.id.order_comment_btn) ImageView commentBtn;
    @BindView(R.id.order_confirm_btn) TextView confirmBtn;
    @BindView(R.id.order_finish_btn) TextView finishBtn;
    @BindView(R.id.order_overtime_btn) TextView overtimeBtn;
    @BindView(R.id.order_cancel_btn) TextView cancelBtn;
    @BindView(R.id.order_abandon_btn) TextView abandonBtn;
    @BindView(R.id.order_cib) CommentInputBoard commentInputBoard;
    @BindView(R.id.order_osrv) OrderStateRecyclerView orderStateRecyclerView;
    @BindView(R.id.order_crv) CommentRecyclerView commentRecyclerView;
    @BindView(R.id.order_comment_reply_view) CommentReplyView commentReplyView;
    @BindView(R.id.order_pv) PayView payView;

    @BindView(R.id.order_msv) MyScrollView scrollView;
    @BindView(R.id.order_header) LinearLayout headerLayout;
    @BindView(R.id.order_count_layout) RelativeLayout countLayout;
    @BindView(R.id.order_count_layout_top) RelativeLayout countLayoutTop;

    @OnClick(R.id.order_comment_btn)
    public void showInputBoard(){
        commentInputBoard.show();
    }

    private String orderId;
    UserInfoWithToken me;
    Animation fadeInAnimation;
    MenuItem editMenu;
    DialogPlus editDialog;
    int updateState = 0;

    TaskCountView taskCountView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        initView();
        refreshLayout.setRefreshing(true);
        orderStateRecyclerView.refresh();
        commentRecyclerView.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_order, menu);
        editMenu = menu.getItem(0);
        editMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        editDialog = DialogUtil.createTaskContentDialog(this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                if(input.length() == 0) {
                    toastShort("任务内容不能为空");
                    return;
                }
                KeyBoardUtil.hideKeyBoard(TaskOrderActivity.this);
                if(taskItemView.getContent().equals(input)) {
                    dialogPlus.dismiss();
                    return;
                }
                SchoolTask.updateTaskInfo(orderId, input);
            }
        }, taskItemView.getContent());
        editDialog.show();
        return true;
    }

    @Override
    public void onScroll(int scrollY) {
        if(scrollY >= headerLayout.getHeight()){
            if (taskCountView.getParent() != countLayoutTop) {
                countLayout.removeView(taskCountView);
                countLayoutTop.addView(taskCountView);
                countLayoutTop.setVisibility(View.VISIBLE);
            }
        }else{
            if (taskCountView.getParent() != countLayout) {
                countLayoutTop.removeView(taskCountView);
                countLayout.addView(taskCountView);
                countLayoutTop.setVisibility(View.INVISIBLE);
            }
        }
        if(scrollY > 0) refreshLayout.setEnabled(false);
        else refreshLayout.setEnabled(true);
    }

    private void initView(){
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        scrollView.setOnScrollListener(this);
        taskCountView = new TaskCountView(this, null);
        taskCountView.setOnOrderChangedListener(new TaskCountView.OnOrderChangedListener() {
            @Override
            public void onOrderChanged(String order) {
                commentRecyclerView.refresh();
            }
        });
        countLayout.addView(taskCountView);
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
                SchoolTask.getTaskComment(orderId, taskCountView.getOrder(), page);
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
        commentRecyclerView.setEmptyView(R.layout.empty_comment);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderStateRecyclerView.refresh();
                commentRecyclerView.refresh();
            }
        });
        //评论框初始化
        commentInputBoard.setOppositeView(btnLayout);
        commentInputBoard.setOnCommentListener(new CommentInputBoard.OnCommentListener() {
            @Override
            public void onComment(String comment) {
                if(comment.length() != 0){
                    progressDialog = ProgressDialog.show(TaskOrderActivity.this, "", "评论中...", true, false);
                    SchoolTask.comment(orderId, commentRecyclerView.getParentId(),
                            commentRecyclerView.getToUserId(), comment);
                }
                else{
                    toastShort(getString(R.string.inputTip, "评论"));
                }
            }
        });
        //支付界面
        payView.setTitle("操作确认");
        payView.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(TaskOrderActivity.this, "", "验证中...", true, false);
                SchoolTask.changeTaskOrderState(orderId, updateState, EncriptUtil.getMD5(password));
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
            orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.release), taskOrderInfo.getReleaseTime()));
            if (state == 0) editMenu.setVisible(true);
            else editMenu.setVisible(false);
            if(state < 3){
                bottomLayout.setVisibility(View.VISIBLE);
                bottomLayout.startAnimation(fadeInAnimation);
            }
            else {
                bottomLayout.setVisibility(View.GONE);
                commentRecyclerView.cancelInputBoard();  //防止点击评论弹出
            }
            switch (state){
                case 0:
                    orderStateRecyclerView.add(new OrderStateItem(false, getString(R.string.waitAccept), ""));
                    if(isReleaseUser){
                        cancelBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setOnClickListener(this);
                    }
                    break;
                case 1:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasAccept),
                            taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(false, getString(R.string.waitFinish), ""));
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
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasAccept),
                            taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasFinished),
                            taskOrderInfo.getFinishedTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.waitConfirm),
                            getString(R.string.confirmTip)));
                    if(isReleaseUser) {
                        confirmBtn.setVisibility(View.VISIBLE);
                        confirmBtn.setOnClickListener(this);
                    }
                    break;
                case 3:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasAccept),
                            taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasFinished),
                            taskOrderInfo.getFinishedTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderFinished), ""));
                    break;
                case 4:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderOvertime),
                            taskOrderInfo.getOverTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderLost), ""));
                    break;
                case 5:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderCancel),
                            taskOrderInfo.getCancelTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderLost), ""));
                    break;
                case 6:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasAccept),
                            taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.taskAbandon),
                            taskOrderInfo.getAbandonTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderLost), ""));
                    break;
                case 7:
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.hasAccept), taskOrderInfo.getAcceptTime(), acceptUser));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.taskOvertime), taskOrderInfo.getOverTime()));
                    orderStateRecyclerView.add(new OrderStateItem(true, getString(R.string.orderLost), ""));
                    break;
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewTaskComment(NewTaskCommentEvent event){
        if(progressDialog != null) progressDialog.dismiss();
        if(event.isOk()){
            toastShort(getString(R.string.commentSuccess));
            commentInputBoard.clear();
            taskCountView.addCommentCount();
            commentRecyclerView.refresh();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeTaskOrderState(ChangeTaskOrderStateEvent event){
        if(progressDialog != null) progressDialog.dismiss();
        if(event.isOk()){
            toastShort(getString(R.string.success));
            payView.hide();
            orderStateRecyclerView.refresh();
        }
        else{
            payView.clearPassword();
            toastShort(event.getError());
            switch (event.getCode()){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 304:   //支付密码错误
                    break;
                default:    //其他
                    payView.hide();
                    break;
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskInfo(UpdateTaskInfoEvent event){
        if (event.isOk()){
            if(editDialog.isShowing()) editDialog.dismiss();
            orderStateRecyclerView.refresh();
            toastShort("修改成功");
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
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        getString(R.string.taskCancelConfirm), getString(R.string.taskLostTip),
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 5;
                                payView.show();
                            }
                        });
                break;
            case R.id.order_overtime_btn:
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        getString(R.string.taskOvertimeConfirm), getString(R.string.taskLostTip),
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 7;
                                payView.show();
                            }
                        });
                break;
            case R.id.order_confirm_btn:
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        getString(R.string.taskFinishedConfirm), getString(R.string.taskFinishedTip),
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 3;
                                payView.show();
                            }
                        });
                break;
            case R.id.order_finish_btn:
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        getString(R.string.taskFinishedConfirm), "",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 2;
                                payView.show();
                            }
                        });
                break;
            case R.id.order_abandon_btn:
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        getString(R.string.taskAbandonConfirm), getString(R.string.taskAbandonTip),
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 6;
                                payView.show();
                            }
                        });
                break;
        }
        if( confirmDialog != null) confirmDialog.show();
    }
}
