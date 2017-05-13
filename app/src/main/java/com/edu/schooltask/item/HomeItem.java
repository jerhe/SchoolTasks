package com.edu.schooltask.item;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeItem implements MultiItemEntity, Serializable {
    public final static int LOAD_TIP = 0;
    public final static int NEAR_TASK_SHOW_ITEM = 1;
    public final static int NEAR_TASK_ITEM = 2;
    public final static int LOOK_NUM_ITEM = 3;

    private int itemType;
    public OrderItem orderItem;
    public String text;
    public int lookNum;

    public HomeItem(int itemType){
        this.itemType = itemType;
    }
    public HomeItem(int itemType, OrderItem orderItem){
        this.itemType = itemType;
        this.orderItem = orderItem;
    }


    //loadtip
    public HomeItem(int itemType, String text){
        this.itemType = itemType;
        this.text = text;
    }

    //looknum
    public HomeItem(int itemType, int lookNum){
        this.itemType = itemType;
        this.lookNum = lookNum;
    }

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
