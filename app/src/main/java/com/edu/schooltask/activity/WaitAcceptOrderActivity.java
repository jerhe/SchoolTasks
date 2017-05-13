package com.edu.schooltask.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.AcceptOrderEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class WaitAcceptOrderActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<HomeItem> itemList = new ArrayList<>();

    private Button acceptBtn;

    HomeItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_accept_order);
        EventBus.getDefault().register(this);
        recyclerView = (RecyclerView) findViewById(R.id.wao_rv);
        acceptBtn = (Button) findViewById(R.id.ln_accept_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeAdapter(itemList, mDataCache, this);
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        item = (HomeItem) intent.getSerializableExtra("order");
        item.setItemType(HomeItem.NEAR_TASK_ITEM);
        itemList.add(item);
        itemList.add(new HomeItem(HomeItem.LOOK_NUM_ITEM, item.orderItem.getLookNum()));

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
}
