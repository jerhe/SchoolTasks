package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import adapter.OrderAdapter;
import item.OrderItem;
import utils.TextUtil;
import view.OrderTypeMenu;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class AllOrderFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<OrderItem> typeOrderList = new ArrayList<>();
    private List<OrderItem> allOrderList = new ArrayList<>();
    private OrderTypeMenu orderTypeMenu;
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
        orderTypeMenu = (OrderTypeMenu) view.findViewById(R.id.ao_menu);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.ao_srl);
        recyclerView = (RecyclerView) view.findViewById(R.id.ao_rv);
        tipText = (TextView) view.findViewById(R.id.ao_tip);
        swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                getResources().getDisplayMetrics()));   //改变刷新圆圈高度
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final OrderAdapter orderAdapter = new OrderAdapter(R.layout.item_order, typeOrderList);
        orderAdapter.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.header_order_empty,null));
        orderAdapter.openLoadAnimation();
        recyclerView.setAdapter(orderAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public boolean isScroll = false;
            int direction = 0;  //滚动方向  用于判断拖曳
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == 0) {
                    isScroll = false;
                    direction = 0;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy < 0) {
                    if(direction == -1) isScroll = false;
                    direction = 1;
                }
                if(dy > 0){
                    if(direction == 1) isScroll = false;
                    direction = -1;
                }
                if(!isScroll) {
                    isScroll = true;
                    if (dy < 0) {
                        if(!orderTypeMenu.isShown()){
                            orderTypeMenu.setVisibility(View.VISIBLE);
                            orderTypeMenu.startAnimation(
                                    AnimationUtils.loadAnimation(getContext(),R.anim.translate_up_to_down));
                        }
                    } else if(dy > 0){
                        if(orderTypeMenu.isShown()){
                            orderTypeMenu.setVisibility(View.INVISIBLE);
                            orderTypeMenu.startAnimation(
                                    AnimationUtils.loadAnimation(getContext(),R.anim.translate_down_to_up));
                        }
                    }
                    if(dy == 0) {
                        isScroll = false;
                        direction = 0;
                    }
                }
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderTypeMenu.setOnMenuSelectedListener(new OrderTypeMenu.OnMenuItemSelectedListener() {
            @Override
            public void OnMenuItemSelected(int position) {
                typeOrderList.clear();
                typeOrderList.addAll(allOrderList);
                switch (position){
                    case 0:    //所有
                        orderAdapter.notifyDataSetChanged();
                        break;
                    default:    //position对应订单状态
                        for(OrderItem orderItem : allOrderList){
                            if(orderItem.getState() != position - 1){
                                int index = typeOrderList.indexOf(orderItem) + 1;
                                typeOrderList.remove(orderItem);
                                orderAdapter.notifyItemRemoved(index);
                                orderAdapter.notifyItemRangeRemoved(1,index);
                            }
                        }
                        break;
                }
                checkEmpty();
            }
        });
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextUtil.toast(getContext(), typeOrderList.get(position).getContent());
            }
        });
        //TEST
        OrderItem orderItem1 = new OrderItem("111",1,"标题1","内容1内容1内容1内容1内容1内容1内容1内容1",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        OrderItem orderItem2 = new OrderItem("111",0,"标题2","内容我是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊是一条内容啊啊啊啊啊啊啊啊2",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem3 = new OrderItem("111",0,"标题3","内容3",3.5f, OrderItem.STATE_CANCEL);
        OrderItem orderItem4 = new OrderItem("111",1,"标题4","内容4",3.5f, OrderItem.STATE_WAIT_PAY);
        OrderItem orderItem5 = new OrderItem("111",1,"标题5","内容5",3.5f, OrderItem.STATE_WAIT_ASSESS);
        OrderItem orderItem6 = new OrderItem("111",1,"标题6","内容6",3.5f, OrderItem.STATE_WAIT_ACCEPT);
        allOrderList.add(orderItem1);
        allOrderList.add(orderItem2);
        allOrderList.add(orderItem3);
        allOrderList.add(orderItem4);
        allOrderList.add(orderItem5);
        allOrderList.add(orderItem6);

        orderTypeMenu.setSelectItem(0);
    }

    public void checkEmpty(){
        if(typeOrderList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }
}
