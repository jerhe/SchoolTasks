package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import adapter.OrderAdapter;
import item.OrderItem;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class WaitOrderFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    List<OrderItem> waitOrderItems = new ArrayList<>();


    public WaitOrderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_wait_order,container,false);
            init();
        }
        return view;
    }


    private void init(){
        recyclerView= (RecyclerView)view.findViewById(R.id.wo_rv);
        OrderAdapter orderAdapter= new OrderAdapter(R.layout.item_order,waitOrderItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderAdapter);

        //TEST
        OrderItem orderItem1 = new OrderItem("111","标题1","内容1内容1内容1内容1内容1内容1内容1内容1",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        OrderItem orderItem2 = new OrderItem("111","标题2","内容我是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊啊啊啊啊2",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem3 = new OrderItem("111","标题3","内容3",3.5f, OrderItem.STATE_CANCEL);
        OrderItem orderItem4 = new OrderItem("111","标题4","内容4",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem5 = new OrderItem("111","标题5","内容5",3.5f, OrderItem.STATE_FINISHED);
        OrderItem orderItem6 = new OrderItem("111","标题6","内容6",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        waitOrderItems.add(orderItem1);
        waitOrderItems.add(orderItem2);
        waitOrderItems.add(orderItem3);
        waitOrderItems.add(orderItem4);
        waitOrderItems.add(orderItem5);
        waitOrderItems.add(orderItem6);
    }

}
