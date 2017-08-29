package com.edu.schooltask.ui.activity;

import android.os.Bundle;

import com.baoyz.widget.PullRefreshLayout;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.ui.view.recyclerview.VoucherRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.event.voucher.GetAvailableVouchersEvent;


public class VoucherActivity extends BaseActivity {
    @BindView(R.id.voucher_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.voucher_vrv) VoucherRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refresh();
            }
        });
        recyclerView.setOnGetDataListener(new BaseRecyclerView.OnGetDataListener() {
            @Override
            public void onGetData() {
                SchoolTask.getAvailableVouchers();
            }
        });
        recyclerView.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetAvailableVouchers(GetAvailableVouchersEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            List<Voucher> vouchers = GsonUtil.toVoucherList(event.getData());
            recyclerView.add(vouchers);
        }
        else{
            toastShort(event.getError());
        }
    }
}
