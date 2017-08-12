package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskCommentList;
import com.edu.schooltask.beans.TaskCount;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.item.TaskCountItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CommentInputBoard;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
    @BindView(R.id.wao_srl) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.wao_rv) RecyclerView recyclerView;
    @BindView(R.id.wao_child_srl) SwipeRefreshLayout childSwipeRefreshLayout;
    @BindView(R.id.wao_child_rv) RecyclerView childRecyclerView;
    @BindView(R.id.wao_accept_btn) Button acceptBtn;
    @BindView(R.id.wao_comment_btn) Button commentBtn;
    @BindView(R.id.wao_btn_layout) RelativeLayout btnLayout;
    @BindView(R.id.wao_ib) CommentInputBoard commentInputBoard;

    @OnClick(R.id.wao_accept_btn)
    public void accept(){
        if("接单".equals(acceptBtn.getText())){
            UserInfo user = UserUtil.getLoginUser();
            if(user != null){
                if(user.getUserId().equals(item.getTaskItem().getUser().getUserId())){
                    toastShort("不能接自己发布的任务哦");
                }
                else{
                    DialogUtil.createTextDialog(WaitAcceptOrderActivity.this, "提示", "确定接受该任务吗",
                            "请确保自身有能力完成该任务，接单后对于订单的疑问可联系发布人", "确定", new DialogUtil.OnClickListener() {
                                @Override
                                public void onClick(DialogPlus dialogPlus) {
                                    SchoolTask.acceptTask(item.getTaskItem().getOrderId());
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
        btnLayout.setVisibility(View.INVISIBLE);
        commentInputBoard.setVisibility(View.VISIBLE);
        KeyBoardUtil.showKeyBoard(commentInputBoard.getInputText());
    }

    private HomeAdapter adapter;
    private HomeAdapter childAdapter;
    private List<HomeItem> itemList = new ArrayList<>();
    private List<HomeItem> childItemList = new ArrayList<>();

    HomeItem item;
    HomeItem countItem;

    int page = 0;

    long parentId;  //查看回复的刷新依据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_accept_order);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeAdapter(itemList, mDataCache, this);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.openLoadAnimation();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tc_child_count:
                        swipeRefreshLayout.setVisibility(View.INVISIBLE);
                        childSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        btnLayout.setVisibility(View.INVISIBLE);
                        commentInputBoard.setVisibility(View.VISIBLE);
                        parentId = itemList.get(position).getTaskComment().getId();
                        setTitle("查看回复");
                        commentInputBoard.setParentId(parentId,
                                itemList.get(position).getTaskComment().getCommentUser().getName());
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                        childItemList.clear();
                        childAdapter.notifyDataSetChanged();
                        getOrderChildComment();
                        break;
                    case R.id.ui_layout:
                        Intent intent = new Intent(WaitAcceptOrderActivity.this, UserActivity.class);
                        if(position == 0){
                            TaskItem taskItem = itemList.get(position).getTaskItem();
                            intent.putExtra("user", taskItem.getUser());
                        }
                        else{
                            TaskComment taskComment= itemList.get(position).getTaskComment();
                            UserInfoBase commentUser = taskComment.getCommentUser();
                            intent.putExtra("user", commentUser);
                        }
                        startActivity(intent);
                        break;
                }
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getTaskComment();
            }
        },recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(position>1){
                    if(!commentInputBoard.isShown()){
                        btnLayout.setVisibility(View.INVISIBLE);
                        commentInputBoard.setVisibility(View.VISIBLE);
                    }
                    TaskComment taskComment = itemList.get(position).getTaskComment();
                    commentInputBoard.setParentId(taskComment.getId(), taskComment.getCommentUser().getName());
                    commentInputBoard.requestFocus();
                    KeyBoardUtil.showKeyBoard(commentInputBoard.getInputText());
                }
            }
        });

        childRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        childAdapter = new HomeAdapter(childItemList, mDataCache, this);
        childAdapter.openLoadAnimation();
        childRecyclerView.setAdapter(childAdapter);
        childAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.ui_layout:
                        Intent intent = new Intent(WaitAcceptOrderActivity.this, UserActivity.class);
                        TaskComment taskComment = childItemList.get(position).getTaskComment();
                        UserInfoBase commentUser = taskComment.getCommentUser();
                        intent.putExtra("user", commentUser);
                        startActivity(intent);
                        break;
                }
            }
        });
        childAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!commentInputBoard.isShown()){
                    btnLayout.setVisibility(View.INVISIBLE);
                    commentInputBoard.setVisibility(View.VISIBLE);
                }
                TaskComment taskComment = childItemList.get(position).getTaskComment();
                commentInputBoard.setParentId(
                        taskComment.getId(), taskComment.getCommentUser().getName());
                commentInputBoard.requestFocus();
                KeyBoardUtil.showKeyBoard(commentInputBoard.getInputText());
            }
        });


        //初始化任务信息和评论数和浏览数
        final Intent intent = getIntent();
        item = (HomeItem) intent.getSerializableExtra("task");
        item.setItemType(HomeItem.TASK_INFO_ITEM);
        countItem = new HomeItem(HomeItem.COUNT_ITEM, new TaskCountItem());
        itemList.add(item);
        itemList.add(countItem);

        commentInputBoard.setOnBtnClickListener(new CommentInputBoard.OnBtnClickListener() {
            @Override
            public void btnClick(long parentId, String comment) {
                if(comment.length() != 0){
                    SchoolTask.comment(item.getTaskItem().getOrderId(), parentId, comment);
                }
                else{
                    toastShort("请输入评论");
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        getTaskComment();
                    }
                },recyclerView);
                refresh();
            }
        });

        childSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                childItemList.clear();
                getOrderChildComment();
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
        if(childRecyclerView.isShown()){
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            if (childSwipeRefreshLayout.isRefreshing()) childSwipeRefreshLayout.setRefreshing(false);
            childSwipeRefreshLayout.setVisibility(View.INVISIBLE);
            setTitle("任务详情");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            commentInputBoard.clearParentId();
            return;
        }
        if(commentInputBoard.isShown()){
            commentInputBoard.setVisibility(View.INVISIBLE);
            commentInputBoard.clearParentId();
            btnLayout.setVisibility(View.VISIBLE);
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
            commentInputBoard.clear();
            if(swipeRefreshLayout.isShown()) {
                refresh();
            }
            else {
                childItemList.clear();
                getOrderChildComment();
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskComment(GetTaskCommentEvent event){
        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            if(taskCommentList.getOrderId().equals(item.getTaskItem().getOrderId())){
                if(page == 0){
                    while(itemList.size()>2) itemList.remove(itemList.size()-1);
                }
                page ++;
                List<TaskComment> taskComments = taskCommentList.getTaskComments();
                for(TaskComment taskComment : taskComments){
                    HomeItem homeItem = new HomeItem(HomeItem.COMMENT, taskComment);
                    itemList.add(homeItem);
                }
                adapter.loadMoreComplete();
                if(taskComments.size() == 0){
                    adapter.loadMoreEnd();
                }
                adapter.notifyDataSetChanged();
            }
        }
        else{
            toastShort(event.getError());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskChildComment(GetTaskChildCommentEvent event){
        if(childSwipeRefreshLayout.isRefreshing()) childSwipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            TaskCommentList taskCommentList = GsonUtil.toTaskCommentList(event.getData());
            List<TaskComment> taskComments = taskCommentList.getTaskComments();
            for(TaskComment taskComment : taskComments){
                HomeItem homeItem = new HomeItem(HomeItem.COMMENT, taskComment, parentId);
                childItemList.add(homeItem);
            }
            childAdapter.notifyDataSetChanged();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskCount(GetTaskCountEvent event){
        if(event.isOk()){
            TaskCount taskCount = GsonUtil.toTaskCount(event.getData());
            itemList.set(1,new HomeItem(HomeItem.COUNT_ITEM, new TaskCountItem(taskCount.getCommentCount(), taskCount.getLookCount())));
            adapter.notifyDataSetChanged();
        }
    }



    private void getTaskComment(){
        String orderId = item.getTaskItem().getOrderId();
        SchoolTask.getTaskComment(orderId, page);
        if(page == 0){
            SchoolTask.getTaskCount(orderId);
        }
    }

    private void getOrderChildComment(){
        SchoolTask.getTaskChildComment(item.getTaskItem().getOrderId(), parentId);
    }

    private void refresh(){
        page = 0;
        getTaskComment();
    }
}
