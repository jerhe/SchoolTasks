package adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

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
        TextView typeText = helper.getView(R.id.ao_type);
        GradientDrawable typeGrad = (GradientDrawable)typeText.getBackground();
        if(item.getType() == 0) typeGrad.setColor(Color.parseColor("#FFA500"));
        else typeGrad.setColor(Color.parseColor("#1B9DFF"));
        typeText.setText(item.getType() == 0 ? "发" : "接");
        helper.setText(R.id.ao_title, item.getTitle());
        helper.setText(R.id.ao_content, item.getContent());
        helper.setText(R.id.ao_cost, "￥"+item.getCost());
        helper.setText(R.id.ao_state, item.getStateStr());
    }
}
