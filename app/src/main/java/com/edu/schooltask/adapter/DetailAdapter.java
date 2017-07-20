package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.view.TextItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class DetailAdapter extends BaseQuickAdapter<Detail, BaseViewHolder> {
    public DetailAdapter(int layoutResId, List<Detail> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Detail item) {
        TextItem idText = helper.getView(R.id.detail_id);
        TextItem timeText = helper.getView(R.id.detail_time);
        TextItem explanationText = helper.getView(R.id.detail_explanation);
        TextItem typeText = helper.getView(R.id.detail_type);

        idText.setText(item.getId());
        timeText.setText(item.getCreateTime());
        explanationText.setText(item.getExplanation());
        typeText.setText(item.getMoney().toString() + "元");
        switch (item.getType()){
            case 0:
                typeText.setName("充值");
                break;
            case 1:
                typeText.setName("转出");
                break;
            case 2:
                typeText.setName("收入");
                break;
            case 3:
                typeText.setName("支出");
                break;
        }
    }
}
