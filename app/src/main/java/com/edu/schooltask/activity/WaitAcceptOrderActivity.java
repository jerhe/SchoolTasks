package com.edu.schooltask.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.AcceptOrderEvent;
import com.edu.schooltask.event.GetOrderChildCommentEvent;
import com.edu.schooltask.event.GetOrderCountEvent;
import com.edu.schooltask.event.GetOrderCommentEvent;
import com.edu.schooltask.event.NewOrderCommentEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderComment;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.view.InputBoard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaitAcceptOrderActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout childSwipeRefreshLayout;
    private RecyclerView childRecyclerView;
    private HomeAdapter adapter;
    private HomeAdapter childAdapter;
    private List<HomeItem> itemList = new ArrayList<>();
    private List<HomeItem> childItemList = new ArrayList<>();

    private Button acceptBtn;
    private Button assessBtn;
    private RelativeLayout btnLayout;
    private InputBoard inputBoard;
    HomeItem item;
    HomeItem countItem;

    int pageIndex = 0;

    long parentId;  //查看回复的刷新依据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_accept_order);
        EventBus.getDefault().register(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.wao_srl);
        recyclerView = (RecyclerView) findViewById(R.id.wao_rv);
        childSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.wao_child_srl);
        childRecyclerView = (RecyclerView) findViewById(R.id.wao_child_rv);
        acceptBtn = (Button) findViewById(R.id.wao_accept_btn);
        assessBtn = (Button) findViewById(R.id.wao_assess_btn);
        inputBoard = (InputBoard) findViewById(R.id.wao_ib);
        btnLayout = (RelativeLayout) findViewById(R.id.wao_btn_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeAdapter(itemList, mDataCache, this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.ocp_child_count:
                        swipeRefreshLayout.setVisibility(View.INVISIBLE);
                        childSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        btnLayout.setVisibility(View.INVISIBLE);
                        inputBoard.setVisibility(View.VISIBLE);
                        parentId = itemList.get(position).orderComment.getCommentId();
                        setTitle("查看回复");
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
                        OrderComment orderComment = itemList.get(position).orderComment;
                        intent.putExtra("user", new User(orderComment.getUserId(), orderComment.getUserName()));
                        startActivity(intent);
                        break;
                }
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getOrderComment();
            }
        },recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(position>1){
                    if(!inputBoard.isShown()){
                        btnLayout.setVisibility(View.INVISIBLE);
                        inputBoard.setVisibility(View.VISIBLE);
                    }
                    OrderComment orderComment = itemList.get(position).orderComment;
                    inputBoard.setParentId(orderComment.getCommentId(),orderComment.getUserName());
                    inputBoard.requestFocus();
                    KeyBoardUtil.inputKeyBoard(inputBoard.getInputText());
                }
            }
        });

        childRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        childAdapter = new HomeAdapter(childItemList, mDataCache, this);
        childRecyclerView.setAdapter(childAdapter);
        childAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.ui_layout:
                        Intent intent = new Intent(WaitAcceptOrderActivity.this, UserActivity.class);
                        OrderComment orderComment = childItemList.get(position).orderComment;
                        intent.putExtra("user", new User(orderComment.getUserId(), orderComment.getUserName()));
                        startActivity(intent);
                        break;
                }
            }
        });
        childAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!inputBoard.isShown()){
                    btnLayout.setVisibility(View.INVISIBLE);
                    inputBoard.setVisibility(View.VISIBLE);
                }
                OrderComment orderComment = childItemList.get(position).orderComment;
                inputBoard.setParentId(orderComment.getCommentId(),orderComment.getUserName());
                inputBoard.requestFocus();
                KeyBoardUtil.inputKeyBoard(inputBoard.getInputText());
            }
        });

        final Intent intent = getIntent();
        item = (HomeItem) intent.getSerializableExtra("order");
        item.setItemType(HomeItem.NEAR_TASK_ITEM);
        countItem = new HomeItem(HomeItem.COUNT_ITEM, item.orderItem.getLookCount());
        itemList.add(item);
        itemList.add(countItem);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("接单".equals(acceptBtn.getText())){
                    User user = mDataCache.getUser();
                    if(user != null){
                        if(user.getUserId().equals(item.orderItem.getReleaseUser().getUserId())){
                            toastShort("不能接自己发布的任务哦");
                        }
                        else
                            HttpUtil.acceptOrder(user.getToken(), item.orderItem.getId(), user.getUserId());
                    }
                    else{
                        toastShort("请先登录");
                        openActivity(LoginActivity.class);
                    }
                }
            }
        });

        assessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLayout.setVisibility(View.INVISIBLE);
                inputBoard.setVisibility(View.VISIBLE);
                KeyBoardUtil.inputKeyBoard(inputBoard.getInputText());
            }
        });

        inputBoard.setOnBtnClickListener(new InputBoard.OnBtnClickListener() {
            @Override
            public void btnClick(long parentId, String text) {
                User user = mDataCache.getUser();
                if(user != null){
                    if(text.length() != 0){
                        HttpUtil.newOrderComment(user.getToken(), item.orderItem.getId(),
                                user.getUserId(), parentId, text);
                    }
                    else{
                        toastShort("请输入评论");
                    }
                }
                else{
                    toastShort("请先登录");
                    openActivity(LoginActivity.class);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
            if(inputBoard.parentId == 0){
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
                return;
            }
            else{
                inputBoard.clearParentId();
                return;
            }
        }
        if(inputBoard.isShown()){
            inputBoard.setVisibility(View.INVISIBLE);
            inputBoard.clearParentId();
            btnLayout.setVisibility(View.VISIBLE);
        }
        else{
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAcceptOrder(AcceptOrderEvent event){
        if(event.isOk()){
            toastShort("接单成功");
            acceptBtn.setText("已接单");
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewOrderComment(NewOrderCommentEvent event){
        if(event.isOk()){
            toastShort("评论成功");
            inputBoard.clear();
            if(swipeRefreshLayout.isShown()) refresh();
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
    public void onGetOrderComment(GetOrderCommentEvent event){
        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            pageIndex ++;
            JSONObject data = event.getData();
            try {
                JSONArray array = data.getJSONArray("ocs");
                for(int i=0; i<array.length(); i++){
                    JSONObject ocJSON = array.getJSONObject(i);
                    OrderComment orderComment = new OrderComment(ocJSON.getLong("comment_id"),
                            ocJSON.getString("order_id"), ocJSON.getString("user_id"),
                            ocJSON.getString("user_name"),
                            ocJSON.getString("user_school"), ocJSON.getInt("user_sex"),
                            ocJSON.getLong("parent_id"), ocJSON.getString("comment"),
                            ocJSON.getInt("child_count"), ocJSON.getString("create_time"));
                    itemList.add(new HomeItem(HomeItem.COMMENT, orderComment));
                }
                adapter.loadMoreComplete();
                if(array.length() < 5){
                    adapter.loadMoreEnd();
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            adapter.loadMoreFail();
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOrderChildComment(GetOrderChildCommentEvent event){
        if(childSwipeRefreshLayout.isRefreshing()) childSwipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            JSONObject data = event.getData();
            try {
                JSONArray array = data.getJSONArray("ocs");
                for(int i=0; i<array.length(); i++){
                    JSONObject ocJSON = array.getJSONObject(i);
                    OrderComment orderComment = new OrderComment(ocJSON.getLong("comment_id"),
                            ocJSON.getString("order_id"), ocJSON.getString("user_id"),
                            ocJSON.getString("user_name"), ocJSON.getString("to_user_name"),
                            ocJSON.getString("user_school"), ocJSON.getInt("user_sex"),
                            ocJSON.getLong("parent_id"), ocJSON.getString("comment"),
                            ocJSON.getInt("child_count"), ocJSON.getString("create_time"));
                    childItemList.add(new HomeItem(HomeItem.COMMENT, orderComment));
                }
                childAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOrderCount(GetOrderCountEvent event){
        if(event.isOk()){
            try {
                int commentCount = event.getData().getInt("comment_count");
                int lookCount = event.getData().getInt("look_count");
                itemList.get(1).commentCount = commentCount;
                itemList.get(1).lookCount = lookCount;
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void getOrderComment(){
        HttpUtil.getOrderComment(item.orderItem.getId(), pageIndex);
        if(pageIndex == 0){
            HttpUtil.getOrderCount(item.orderItem.getId());
        }
    }

    private void getOrderChildComment(){
        HttpUtil.getOrderChildComment(item.orderItem.getId(), parentId);
    }

    private void refresh(){
        pageIndex = 0;
        itemList.clear();
        itemList.add(item);
        itemList.add(countItem);
        getOrderComment();
    }
}
