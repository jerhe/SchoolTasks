package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseCommentActivity;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskInfo;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.beans.task.TaskOrderInfo;
import com.edu.schooltask.item.OrderStateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.MyScrollView;
import com.edu.schooltask.view.PayPasswordView;
import com.edu.schooltask.view.PayView;
import com.edu.schooltask.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.view.recyclerview.OrderStateRecyclerView;
import com.edu.schooltask.view.task.TaskItemView;
import com.orhanobut.dialogplus.DialogPlus;

import butterknife.BindView;
import server.api.client.TaskClient;
import server.api.result.Result;

public class TaskOrderActivity extends BaseCommentActivity implements View.OnClickListener, MyScrollView.OnScrollListener{
    @BindView(R.id.order_id) TextView orderIdText;
    @BindView(R.id.order_tiv) TaskItemView taskItemView;
    @BindView(R.id.order_hide_layout) LinearLayout hideLayout;
    @BindView(R.id.order_hide_content) TextView hideContentText;
    @BindView(R.id.order_bottom) RelativeLayout bottomLayout;
    @BindView(R.id.order_confirm_btn) TextView confirmBtn;
    @BindView(R.id.order_finish_btn) TextView finishBtn;
    @BindView(R.id.order_overtime_btn) TextView overtimeBtn;
    @BindView(R.id.order_cancel_btn) TextView cancelBtn;
    @BindView(R.id.order_abandon_btn) TextView abandonBtn;
    @BindView(R.id.order_osrv) OrderStateRecyclerView stateRV;
    @BindView(R.id.order_pv) PayView payView;

    private Result updateTaskInfoResult = new Result(true) {
        @Override
        public void onResponse(int id) {}

        @Override
        public void onSuccess(int id, Object data) {
            if(editDialog.isShowing()) editDialog.dismiss();
            stateRV.refresh();
            toastShort("修改成功");
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result getOrderInfoResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            stateRV.clear();
        }

        @Override
        public void onSuccess(int id, Object data) {
            setViews(GsonUtil.toTaskOrder(data));
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result changeOrderStateResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort(s(R.string.success));
            payView.hide();
            stateRV.refresh();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            payView.clearPassword();
            toastShort(error);
            switch (code){
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
    };

    private TaskItem taskItem;
    private UserInfoWithToken me;
    private Animation fadeInAnimation;
    private MenuItem editMenu;
    private DialogPlus editDialog;
    private int updateState = 0;

    private ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_task_order;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        taskItem = new TaskItem();
        taskItem.setOrderId(intent.getStringExtra("orderId"));
        initView();
        super.init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_order, menu);
        editMenu = menu.getItem(0);
        editMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean menuClick(int menuId) {
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
                TaskClient.updateTaskInfo(updateTaskInfoResult, taskItem.getOrderId(), input);
            }
        }, taskItemView.getContent());
        editDialog.show();
        return true;
    }

    @Override
    public int[] defaultCount() {
        return new int[]{0, 0};
    }

    @Override
    public void comment() {
        openOrderCommentActivity(taskItem.getOrderId());
    }

    @Override
    public void getCommentList(Result result, int page) {
        TaskClient.getTaskComment(result, taskItem.getOrderId(), countView.getOrder(), page);
    }

    @Override
    public void onCommentClick(Comment comment) {
        openOrderCommentActivity(taskItem.getOrderId(), comment.getId(),
                comment.getUserInfo().getName(), true);
    }

    @Override
    public void openReply(Comment comment) {
        openReplyActivity(COMMENT_ORDER, comment.getId(), taskItem.getOrderId(), 0,
                comment.getReplyCount(), comment.getUserInfo().getName());
    }

    @Override
    public void refresh(){
        stateRV.refresh();
        commentRecyclerView.refresh();
    }

    private void initView(){
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        me = UserUtil.getLoginUser();
        //状态列表初始化
        stateRV.setNestedScrollingEnabled(false);
        stateRV.setOnRequestDataListener(new BaseRecyclerView.OnRequestDataListener() {
            @Override
            public void onRequestData() {
                TaskClient.getOrderInfo(getOrderInfoResult, taskItem.getOrderId());
            }
        });
        //支付界面
        payView.setTitle("操作确认");
        payView.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(TaskOrderActivity.this, "", "验证中...", true, false);
                TaskClient.changeTaskOrderState(changeOrderStateResult, taskItem.getOrderId(), updateState,
                        EncriptUtil.getMD5(password));
            }
        });
    }

    private void setViews(TaskOrderInfo taskOrderInfo){
        TaskInfo taskInfo = taskOrderInfo.getTaskInfo();
        if(StringUtil.isEmpty(taskInfo.getHideContent())){
            hideLayout.setVisibility(View.GONE);
        }
        else{
            hideLayout.setVisibility(View.VISIBLE);
            hideContentText.setText(taskInfo.getHideContent());
        }
        abandonBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        confirmBtn.setVisibility(View.GONE);
        finishBtn.setVisibility(View.GONE);
        overtimeBtn.setVisibility(View.GONE);
        boolean isReleaseUser = me.getUserId().equals(taskOrderInfo.getReleaseId());
        orderIdText.setText(taskItem.getOrderId());
        taskItemView.setAll(taskOrderInfo.toTaskItem(), true);
        //评论数和浏览数
        countView.setCommentCount(taskInfo.getCommentCount());
        countView.setLookCount(taskInfo.getLookCount());
        //状态
        int state = taskOrderInfo.getState();
        //接单人信息
        UserInfo acceptUser = taskOrderInfo.getAcceptUserInfo();
        stateRV.add(new OrderStateItem(true, getString(R.string.task_release),
                taskOrderInfo.getReleaseTime()));
        if (state == 0) editMenu.setVisible(true);
        else editMenu.setVisible(false);
        if(state < 3){
            bottomLayout.setVisibility(View.VISIBLE);
            bottomLayout.startAnimation(fadeInAnimation);
        }
        else {
            bottomLayout.setVisibility(View.GONE);
            commentRecyclerView.setItemClickListener(null);
        }
        switch (state){
            case 0:
                stateRV.add(new OrderStateItem(false, getString(R.string.task_wait_accept), ""));
                if(isReleaseUser){
                    cancelBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setOnClickListener(this);
                }
                break;
            case 1:
                stateRV.add(new OrderStateItem(true, s(R.string.task_accepted),
                        taskOrderInfo.getAcceptTime(), acceptUser));
                stateRV.add(new OrderStateItem(false, s(R.string.task_wait_finish), ""));
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
                stateRV.add(new OrderStateItem(true, s(R.string.task_accepted),
                        taskOrderInfo.getAcceptTime(), acceptUser));
                stateRV.add(new OrderStateItem(true, s(R.string.task_finished),
                        taskOrderInfo.getFinishedTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_wait_confirm),
                        getString(R.string.task_confirm_tip)));
                if(isReleaseUser) {
                    confirmBtn.setVisibility(View.VISIBLE);
                    confirmBtn.setOnClickListener(this);
                }
                break;
            case 3:
                stateRV.add(new OrderStateItem(true, s(R.string.task_accepted),
                        taskOrderInfo.getAcceptTime(), acceptUser));
                stateRV.add(new OrderStateItem(true, s(R.string.task_finished),
                        taskOrderInfo.getFinishedTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_order_finished), ""));
                break;
            case 4:
                stateRV.add(new OrderStateItem(true, s(R.string.task_order_overtime),
                        taskOrderInfo.getOverTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_lost), ""));
                break;
            case 5:
                stateRV.add(new OrderStateItem(true, s(R.string.task_cancel),
                        taskOrderInfo.getCancelTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_lost), ""));
                break;
            case 6:
                stateRV.add(new OrderStateItem(true, s(R.string.task_accepted),
                        taskOrderInfo.getAcceptTime(), acceptUser));
                stateRV.add(new OrderStateItem(true, s(R.string.task_abandon),
                        taskOrderInfo.getAbandonTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_lost), ""));
                break;
            case 7:
                stateRV.add(new OrderStateItem(true, s(R.string.task_accepted), taskOrderInfo.getAcceptTime(), acceptUser));
                stateRV.add(new OrderStateItem(true, s(R.string.task_overtime), taskOrderInfo.getOverTime()));
                stateRV.add(new OrderStateItem(true, s(R.string.task_lost), ""));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        DialogPlus confirmDialog = null;
        switch (v.getId()){
            case R.id.order_cancel_btn:
                confirmDialog = DialogUtil.createTextDialog(this, getString(R.string.tip),
                        s(R.string.task_cancel_confirm), s(R.string.task_lost_tip),
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
                        s(R.string.task_overtime_confirm), s(R.string.task_lost_tip),
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
                        s(R.string.task_finished_confirm), s(R.string.task_finished_tip),
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                updateState = 3;
                                payView.show();
                            }
                        });
                break;
            case R.id.order_finish_btn:
                confirmDialog = DialogUtil.createTextDialog(this, s(R.string.tip),
                        s(R.string.task_finished_confirm), "",
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
                        s(R.string.task_abandon_confirm), s(R.string.task_abandon_tip),
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
