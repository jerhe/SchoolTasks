package adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;

import java.util.List;

import item.OrderItem;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class OrderAdapter extends BaseQuickAdapter<OrderItem, BaseViewHolder> {
    public OrderAdapter(int layoutResId, List<OrderItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderItem item) {
        helper.setText(R.id.ao_title, item.getTitle());
        helper.setText(R.id.ao_content, item.getContent());
        helper.setText(R.id.ao_cost, "￥"+item.getCost());
        helper.setText(R.id.ao_state, item.getStateStr());
    }
}
