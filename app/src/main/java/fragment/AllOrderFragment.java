package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class AllOrderFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<OrderItem> allOrderList = new ArrayList<>();
    public AllOrderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_all_order,container,false);
            init();
        }
        return view;
    }

    private void init(){
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.ao_srl);
        recyclerView = (RecyclerView) view.findViewById(R.id.ao_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderAdapter orderAdapter = new OrderAdapter(R.layout.item_order, allOrderList);
        recyclerView.setAdapter(orderAdapter);

        //TEST
        OrderItem orderItem1 = new OrderItem("111","标题1","内容1内容1内容1内容1内容1内容1内容1内容1",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        OrderItem orderItem2 = new OrderItem("111","标题2","内容我是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊啊啊啊啊2",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem3 = new OrderItem("111","标题3","内容3",3.5f, OrderItem.STATE_CANCEL);
        OrderItem orderItem4 = new OrderItem("111","标题4","内容4",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem5 = new OrderItem("111","标题5","内容5",3.5f, OrderItem.STATE_FINISHED);
        OrderItem orderItem6 = new OrderItem("111","标题6","内容6",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        allOrderList.add(orderItem1);
        allOrderList.add(orderItem2);
        allOrderList.add(orderItem3);
        allOrderList.add(orderItem4);
        allOrderList.add(orderItem5);
        allOrderList.add(orderItem6);
    }
}
