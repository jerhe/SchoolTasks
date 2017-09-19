package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Location;
import com.edu.schooltask.utils.StringUtil;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/12.
 */

public class LocationAdapter extends BaseQuickAdapter<Location, BaseViewHolder> {

    public LocationAdapter(@Nullable List<Location> data) {
        super(R.layout.item_location, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Location item) {
        String detail = item.getDetail();
        if(StringUtil.isEmpty(detail)){
            helper.setVisible(R.id.location_detail, false);
            helper.setText(R.id.location_title, item.getCity());
        }
        else{
            helper.setVisible(R.id.location_detail, true);
            helper.setText(R.id.location_title, item.getLocation());
            helper.setText(R.id.location_detail, item.getDetail());
        }
    }
}
