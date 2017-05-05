package adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;

import java.util.List;

import item.FunctionItem;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class FunctionAdapter extends BaseQuickAdapter<FunctionItem, BaseViewHolder> {
    public FunctionAdapter(int layoutResId, List<FunctionItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FunctionItem item) {
        helper.setImageResource(R.id.uf_image, item.getResId());
        helper.setText(R.id.uf_name, item.getName());
    }
}
