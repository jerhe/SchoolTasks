package com.edu.schooltask.fragment.order;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.adapter.OrderWaitAssessAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.item.OrderWaitAssessItem;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class WaitAssessFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<OrderWaitAssessItem> orderWaitAssessItemList = new ArrayList<>();
    public WaitAssessFragment() {
        super(R.layout.fragment_wait_assess);
    }

    @Override
    protected void init(){
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.wa_srl);
        recyclerView = (RecyclerView)view.findViewById(R.id.wa_rv);
        tipText = (TextView) view.findViewById(R.id.wa_tip);
        OrderWaitAssessAdapter orderWaitAssessAdapter = new OrderWaitAssessAdapter(getActivity(), R.layout.item_order_wait_assess, orderWaitAssessItemList);
        recyclerView.setAdapter(orderWaitAssessAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //TEST
        //OrderWaitAssessItem orderWaitAssessItem1 = new OrderWaitAssessItem("111","标题提提提提it","1211121313121");
        //orderWaitAssessItemList.add(orderWaitAssessItem1);
        checkEmpty();
    }

    public void checkEmpty(){
        if(orderWaitAssessItemList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }
}
