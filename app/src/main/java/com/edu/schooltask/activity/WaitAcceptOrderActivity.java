package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.base.BaseCommentActivity;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.view.PayPasswordView;
import com.edu.schooltask.view.PayView;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.MenuRecyclerView;
import com.edu.schooltask.view.task.TaskItemView;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.TaskClient;
import server.api.result.Result;

public class WaitAcceptOrderActivity extends BaseCommentActivity{

    @BindView(R.id.wao_tiv) TaskItemView taskItemView;
    @BindView(R.id.wao_hide_tip) TextView hideContentTip;
    @BindView(R.id.wao_accept_btn) TextView acceptBtn;
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

    private Result getTaskInfoResult = new Result() {
        @Override
        public void onResponse(int id) {
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(int id, Object data) {
            taskItem = GsonUtil.toTaskItem(data);
            taskItemView.setAll(taskItem, true);
            countView.setCount(taskItem.getCommentCount(), taskItem.getLookCount());
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result acceptTaskResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort(getString(R.string.acceptTip));
            payView.hide();
            acceptBtn.setText(s(R.string.task_accepted));
            openActivity(TaskOrderActivity.class, "taskItem", taskItem);
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            payView.clearPassword();
            toastShort(error);
            switch (code){
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
    };

    private ProgressDialog progressDialog;
    private PopupWindow moreMenu;
    private MenuRecyclerView moreRV;

    private TaskItem taskItem;

    @Override
    public int getLayout() {
        return R.layout.activity_wait_accept_order;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        taskItem = (TaskItem) intent.getSerializableExtra("taskItem");
        if(taskItem == null) {
            taskItem = new TaskItem();
            taskItem.setOrderId(intent.getStringExtra("orderId"));
        }
        else taskItemView.setAll(taskItem, true);

        if(StringUtil.isEmpty(taskItem.getHideContent())) hideContentTip.setVisibility(View.GONE);
        else hideContentTip.setVisibility(View.VISIBLE);

        payView.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(WaitAcceptOrderActivity.this, "", "接单中...", true, false);
                TaskClient.acceptTask(acceptTaskResult, taskItem.getOrderId(), EncriptUtil.getMD5(password));
            }
        });

        moreMenu = new PopupWindow(DensityUtil.dipToPx(this, 80),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        moreRV = DialogUtil.createPopupMenu(this, moreMenu, new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                moreMenu.dismiss();
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }
        });
        moreRV.add("分享");
        moreRV.add("举报");
        super.init();
    }

    @Override
    public int[] defaultCount() {
        return new int[]{taskItem.getCommentCount(), taskItem.getLookCount()};
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
        TaskClient.getTaskInfo(getTaskInfoResult, taskItem.getOrderId());
        commentRecyclerView.refresh();
    }


    @Override
    public int createMenu() {
        return R.menu.more;
    }

    @Override
    public boolean menuClick(int menuId) {
        moreMenu.showAsDropDown(toolbar, toolbar.getWidth() - DensityUtil.dipToPx(this, 85),
                DensityUtil.dipToPx(this, 3));
        return true;
    }
}
