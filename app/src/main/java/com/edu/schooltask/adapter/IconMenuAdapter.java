package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;

import java.util.List;

import com.edu.schooltask.item.IconMenuItem;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class IconMenuAdapter extends BaseMultiItemQuickAdapter<IconMenuItem, BaseViewHolder> {

    public IconMenuAdapter(List<IconMenuItem> data) {
        super(data);
        addItemType(IconMenuItem.EMPTY, R.layout.item_icon_menu_empty);
        addItemType(IconMenuItem.HORIZONTAL, R.layout.item_icon_menu_horizontal);
        addItemType(IconMenuItem.VERTICAL, R.layout.item_icon_menu_vertical);
        addItemType(IconMenuItem.SWITCH, R.layout.item_icon_menu_switch);
        addItemType(IconMenuItem.VALUE, R.layout.item_icon_menu_value);
        addItemType(IconMenuItem.TITLE, R.layout.item_icon_menu_title);
    }

    @Override
    protected void convert(BaseViewHolder helper, IconMenuItem item) {
        int type = item.getItemType();
        if(type != IconMenuItem.EMPTY){
            if(type != IconMenuItem.TITLE){
                if(item.getResId() != 0) helper.setImageResource(R.id.im_image, item.getResId());
                else helper.setVisible(R.id.im_image, false);
            }
            helper.setText(R.id.im_name, item.getName());
            if(item.getHint() != null) helper.setText(R.id.im_hint, item.getHint());
            if(type == IconMenuItem.SWITCH){
                helper.setChecked(R.id.im_switch, item.isChecked());
                helper.addOnClickListener(R.id.im_switch);
            }
            if(type == IconMenuItem.VALUE){
                if(item.getValue() != null){
                    helper.setText(R.id.im_value, item.getValue());
                }
            }
        }
    }
}
