package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeItem implements MultiItemEntity {
    public final static int LOAD_TIP = 0;
    public final static int NEAR_TASK_ITEM = 1;

    private int itemType;
    public OrderItem orderItem;
    public String text;

    public HomeItem(int itemType){
        this.itemType = itemType;
    }
    public HomeItem(int itemType, OrderItem orderItem){
        this.itemType = itemType;
        this.orderItem = orderItem;
    }


    public HomeItem(int itemType, String text){
        this.itemType = itemType;
        this.text = text;
    }
    @Override
    public int getItemType() {
        return itemType;
    }
}
