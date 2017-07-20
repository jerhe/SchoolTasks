package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.adapter.StateAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.beans.TaskCommentList;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.item.StateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.edu.schooltask.view.TextItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;
import server.api.task.comment.GetTaskChildCommentEvent;
import server.api.task.comment.GetTaskCommentEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderInfoEvent;


//intent param: order_id
public class TaskOrderActivity extends BaseActivity implements View.OnClickListener{
    private ScrollView orderLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextItem orderIdText;
    private TextItem orderSchoolText;
    private TextView orderContentText;
    private TextItem orderCostText;
    private RecyclerView imageRecyclerView;

    private LinearLayout releaseUserLayout;
    private CircleImageView releaseHeadImage;
    private TextView releaseNameText;
    private TextView releaseSexText;
    private TextView releaseSchoolText;
    private ImageView releaseTalkImage;

    private Button confirmBtn;
    private Button finishBtn;
    private Button overtimeBtn;
    private Button cancelBtn;
    private Button abandonBtn;

    private RecyclerView stateRecyclerView;
    private StateAdapter stateAdapter;
    private List<StateItem> stateList = new ArrayList<>();

    private RecyclerView commentRecyclerView;
    private HomeAdapter commentAdapter;
    private List<HomeItem> comments = new ArrayList<>();
    int commentPage = 0;

    private RecyclerView childCommentRecyclerView;
    private HomeAdapter childCommentAdapter;
    private List<HomeItem> childComments = new ArrayList<>();
    long parentId;

    private String orderId;

    User me;
    UserBaseInfo releaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_order);
        EventBus.getDefault().register(this);
        initView();
        getTaskOrderInfo();
    }

    private void initView(){
        swipeRefreshLayout = getView(R.id.order_srl);
        orderLayout = getView(R.id.order_layout);
        orderIdText = getView(R.id.order_id);
        orderSchoolText = getView(R.id.order_school);
        orderContentText = getView(R.id.order_content);
        orderCostText = getView(R.id.order_cost);
        imageRecyclerView = getView(R.id.order_image_rv);

        releaseUserLayout = getView(R.id.order_release_user_layout);
        releaseHeadImage = getView(R.id.order_release_head);
        releaseNameText = getView(R.id.order_release_name);
        releaseSexText = getView(R.id.order_release_sex);
        releaseSchoolText = getView(R.id.order_release_school);
        releaseTalkImage = getView(R.id.order_release_talk);
        stateRecyclerView = getView(R.id.order_state_rv);
        confirmBtn = getView(R.id.order_confirm_btn);
        finishBtn = getView(R.id.order_finish_btn);
        overtimeBtn = getView(R.id.order_overtime_btn);
        cancelBtn = getView(R.id.order_cancel_btn);
        abandonBtn = getView(R.id.order_abandon_btn);

        me = mDataCache.getUser();
        releaseUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(TaskOrderActivity.this, UserActivity.class);
                userIntent.putExtra("user", releaseUser);
                startActivity(userIntent);
            }
        });
        releaseTalkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!me.getUserId().equals(releaseUser.getUserId())){
                    Intent talkIntent = new Intent(TaskOrderActivity.this, PrivateMessageActivity.class);
                    talkIntent.putExtra("user", releaseUser);
                    startActivity(talkIntent);
                }

            }
        });

        stateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stateRecyclerView.setNestedScrollingEnabled(false);
        stateAdapter = new StateAdapter(R.layout.item_order_state, stateList);
        stateAdapter.openLoadAnimation();
        stateRecyclerView.setAdapter(stateAdapter);
        stateAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UserBaseInfo userBaseInfo = stateList.get(position).getAcceptUser();
                switch (view.getId()){
                    case R.id.os_user_layout:
                        Intent userIntent = new Intent(TaskOrderActivity.this, UserActivity.class);
                        userIntent.putExtra("user", userBaseInfo);
                        startActivity(userIntent);
                        break;
                    case R.id.os_user_talk:
                        if(!me.getUserId().equals(userBaseInfo.getUserId())){
                            Intent talkIntent = new Intent(TaskOrderActivity.this, PrivateMessageActivity.class);
                            talkIntent.putExtra("user", userBaseInfo);
                            startActivity(talkIntent);
                        }
                        break;
                }
            }
        });

        commentRecyclerView = getView(R.id.order_comment_rv);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setNestedScrollingEnabled(false);
        commentAdapter = new HomeAdapter(comments, mDataCache, this);
        commentAdapter.setLoadMoreView(new CustomLoadMoreView());
        commentRecyclerView.setAdapter(commentAdapter);
        commentAdapter.openLoadAnimation();
        commentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                SchoolTask.getTaskComment(orderId, commentPage);
            }
        }, commentRecyclerView);
        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tc_child_count:
                        parentId = comments.get(position).getTaskComment().getId();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                        childComments.clear();
                        childCommentAdapter.notifyDataSetChanged();
                        setTitle("查看回复");
                        childCommentRecyclerView.setVisibility(View.VISIBLE);
                        SchoolTask.getTaskChildComment(orderId, parentId);
                        break;
                    case R.id.ui_layout:
                        Intent intent = new Intent(TaskOrderActivity.this, UserActivity.class);
                        TaskComment taskComment= comments.get(position).getTaskComment();
                        UserBaseInfo commentUser = taskComment.getCommentUser();
                        intent.putExtra("user", commentUser);
                        startActivity(intent);
                        break;
                }
            }
        });

        childCommentRecyclerView = getView(R.id.to_comment_child_rv);
        childCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        childCommentAdapter = new HomeAdapter(childComments, mDataCache, this);
        childCommentRecyclerView.setAdapter(childCommentAdapter);
        childCommentAdapter.openLoadAnimation();
        childCommentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.ui_layout:
                        Intent intent = new Intent(TaskOrderActivity.this, UserActivity.class);
                        TaskComment taskComment = childComments.get(position).getTaskComment();
                        UserBaseInfo commentUser = taskComment.getCommentUser();
                        intent.putExtra("user", commentUser);
                        startActivity(intent);
                        break;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentPage = 0;
                comments.clear();
                commentAdapter.notifyDataSetChanged();
                getTaskOrderInfo();
            }
        });

        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(childCommentRecyclerView.isShown()){
            setTitle("订单详情");
            childCommentRecyclerView.setVisibility(View.INVISIBLE);
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

    /*@Override
    public void onBackPressed() {
        if (payDialog != null){
            if(payDialog.isShowing()){
                payDialog.dismiss();
                EventBus.getDefault().post(new ChangeOrderStateEvent("操作取消"));
            }
            else{
                finish();
            }
        }
        else{
            finish();
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrderInfo(GetTaskOrderInfoEvent event){
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        if(event.isOk()){
            commentAdapter.setEnableLoadMore(true);
            SchoolTask.getTaskComment(orderId, commentPage);
            abandonBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.GONE);
            overtimeBtn.setVisibility(View.GONE);
            TaskOrder taskOrder = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<TaskOrder>(){}.getType());
            releaseUser = taskOrder.getReleaseUser();
            User user = mDataCache.getUser();
            boolean isReleaseUser = user.getUserId().equals(releaseUser.getUserId());
            orderIdText.setText(orderId);
            orderSchoolText.setText(taskOrder.getSchool());
            orderContentText.setText(taskOrder.getContent());
            orderCostText.setText(taskOrder.getCost()+"元");

            //发布人
            if(isReleaseUser){
                releaseTalkImage.setVisibility(View.GONE);
            }
            else{
                releaseTalkImage.setVisibility(View.VISIBLE);
            }
            GlideUtil.setHead(releaseHeadImage.getContext(), releaseUser.getUserId(),releaseHeadImage, true);
            releaseNameText.setText(releaseUser.getName());
            releaseSchoolText.setText(releaseUser.getSchool());
            switch (releaseUser.getSex()){
                case -1:
                    releaseSexText.setText("");
                    break;
                case 0:
                    releaseSexText.setText("♂");
                    releaseSexText.setTextColor(Color.parseColor("#1B9DFF"));
                    break;
                case 1:
                    releaseSexText.setText("♀");
                    releaseSexText.setTextColor(Color.RED);
                    break;
            }

            //图片
            final List<ImageItem> imageItems = new ArrayList<>();
            String imageUrl = SchoolTask.TASK_IMAGE_URL + taskOrder.getOrderId() + "/";
            for(int i = 0; i< taskOrder.getImageNum(); i++){
                imageItems.add(new ImageItem(1,imageUrl + i + ".png"));
            }
            imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            ImageAdapter imageAdapter = new ImageAdapter(R.layout.item_image, imageItems);
            imageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(TaskOrderActivity.this, ImageActivity.class);
                    intent.putExtra("editable", false);
                    intent.putExtra("index", position);
                    intent.putExtra("images", (Serializable) imageItems);
                    startActivity(intent);
                }
            });
            imageRecyclerView.setAdapter(imageAdapter);
            //状态
            int state = taskOrder.getState();
            UserBaseInfo acceptUser = taskOrder.getAcceptUser();
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
        swipeRefreshLayout.setRefreshing(true);
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
        if(event.isOk()){
            TaskCommentList taskCommentList = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<TaskCommentList>(){}.getType());
            if(orderId.equals(taskCommentList.getOrderId())){
                commentPage++;
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                for(TaskComment taskComment : taskComments){
                    HomeItem homeItem = new HomeItem(HomeItem.COMMENT, taskComment);
                    comments.add(homeItem);
                }
                commentAdapter.loadMoreComplete();
                if(taskComments.size() == 0) commentAdapter.loadMoreEnd();
                commentAdapter.notifyDataSetChanged();
            }
        }
        else{
            commentAdapter.loadMoreFail();
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskChildComment(GetTaskChildCommentEvent event){
        if(event.isOk()){
            TaskCommentList taskCommentList = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<TaskCommentList>(){}.getType());
            if(taskCommentList.getOrderId().equals(orderId)) {
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                for (TaskComment taskComment : taskComments) {
                    HomeItem homeItem = new HomeItem(HomeItem.COMMENT, taskComment, parentId);
                    childComments.add(homeItem);
                }
                childCommentAdapter.notifyDataSetChanged();
            }
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
