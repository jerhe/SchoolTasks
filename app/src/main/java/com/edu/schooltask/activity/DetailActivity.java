package com.edu.schooltask.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.DetailAdapter;
import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import java.util.List;

import server.api.client.AccountClient;
import server.api.result.Result;

public class DetailActivity extends BaseActivity {

    private RefreshPageRecyclerView<Detail> recyclerView;

    @Override
    public int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public void init() {

        recyclerView = new RefreshPageRecyclerView<Detail>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List<Detail> list) {
                return new DetailAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                AccountClient.getDetail(result, page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                List<Detail> detailList = GsonUtil.toDetailList(data);
                recyclerView.add(detailList);
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }
        };
        recyclerView.refresh();
        addView(recyclerView);
    }
}
