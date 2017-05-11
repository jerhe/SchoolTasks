package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeItem implements MultiItemEntity {
    public final static int NEAR_TASK_ITEM = 1;

    private int itemType;
    public OrderItem orderItem;

    public HomeItem(int itemType){
        this.itemType = itemType;
    }
    public HomeItem(int itemType, OrderItem orderItem){
        this.itemType = itemType;
        this.orderItem = orderItem;
    }
    @Override
    public int getItemType() {
        return itemType;
    }
}
