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

import adapter.OrderWaitAssessAdapter;
import item.OrderWaitAssessItem;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class WaitAssessFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<OrderWaitAssessItem> orderWaitAssessItemList = new ArrayList<>();
    public WaitAssessFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_wait_assess,container,false);
            init();
        }
        return view;
    }

    private void init(){
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.wa_srl);
        recyclerView = (RecyclerView)view.findViewById(R.id.wa_rv);
        OrderWaitAssessAdapter orderWaitAssessAdapter = new OrderWaitAssessAdapter(getActivity(), R.layout.item_order_wait_assess, orderWaitAssessItemList);
        recyclerView.setAdapter(orderWaitAssessAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TEST
        OrderWaitAssessItem orderWaitAssessItem1 = new OrderWaitAssessItem("111","标题提提提提it","1211121313121");
        OrderWaitAssessItem orderWaitAssessItem2 = new OrderWaitAssessItem("111","标题提提提提it","1211121313121");
        orderWaitAssessItemList.add(orderWaitAssessItem1);
    }
}
