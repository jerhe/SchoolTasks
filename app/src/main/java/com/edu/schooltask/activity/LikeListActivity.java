package com.edu.schooltask.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.UserAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;

import java.util.List;

import server.api.client.DynamicClient;
import server.api.result.Result;

public class LikeListActivity extends BaseActivity {

    private RefreshPageRecyclerView<UserInfo> recyclerView;

    private int dynamicId;

    @Override
    public int getLayout() {
        return R.layout.activity_like_list;
    }

    @Override
    public void init() {
        dynamicId = getIntent().getIntExtra("dynamicId", 0);
        recyclerView = new RefreshPageRecyclerView<UserInfo>(this, false) {
            @Override
            protected BaseQuickAdapter initAdapter(List<UserInfo> list) {
                return new UserAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                DynamicClient.getLikeList(result, dynamicId, page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<UserInfo> list = GsonUtil.toUserInfoList(data);
                recyclerView.add(list.getList(), list.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, UserInfo userInfo) {
                openActivity(UserActivity.class, "name", userInfo.getName());
            }
        };
        addView(recyclerView);
        recyclerView.refresh();
    }
}
