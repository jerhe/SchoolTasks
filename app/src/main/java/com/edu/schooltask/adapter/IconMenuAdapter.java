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

//带图标的菜单适配器
public class IconMenuAdapter extends BaseMultiItemQuickAdapter<IconMenuItem, BaseViewHolder> {

    public IconMenuAdapter(List<IconMenuItem> data) {
        super(data);
        addItemType(IconMenuItem.EMPTY, R.layout.item_icon_menu_empty); //空项，作为分割条
        addItemType(IconMenuItem.HORIZONTAL, R.layout.item_icon_menu_horizontal);   //水平菜单，图标在左,带提示
        addItemType(IconMenuItem.VERTICAL, R.layout.item_icon_menu_vertical);   //竖直菜单，图标在上
        addItemType(IconMenuItem.SWITCH, R.layout.item_icon_menu_switch);   //水平带Switch的菜单，图标在左，带提示
        addItemType(IconMenuItem.VALUE, R.layout.item_icon_menu_value); //水平带文本值的菜单，图标在左，带提示
        addItemType(IconMenuItem.TITLE, R.layout.item_icon_menu_title); //标题，无图标
    }

    @Override
    protected void convert(BaseViewHolder helper, IconMenuItem item) {
        int type = item.getItemType();
        if(type == IconMenuItem.EMPTY) return;
        //图标
        if(item.getResId() == 0) {
            if(type != IconMenuItem.TITLE) helper.setVisible(R.id.im_image, false);
        }
        else {
            helper.setImageResource(R.id.im_image, item.getResId());
        }

        //文本
        helper.setText(R.id.im_name, item.getName());
        //提示
        if(item.getHint() != null) helper.setText(R.id.im_hint, item.getHint());
        //Switch
        if(type == IconMenuItem.SWITCH){
            helper.setChecked(R.id.im_switch, item.isChecked());
            helper.addOnClickListener(R.id.im_switch);
        }
        //Value
        if(item.getValue() != null){
            helper.setText(R.id.im_value, item.getValue());
        }
    }
}
