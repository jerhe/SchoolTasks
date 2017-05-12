package com.edu.schooltask.other;

import com.edu.schooltask.item.OrderItem;

import java.util.Comparator;

/**
 * Created by 夜夜通宵 on 2017/5/10.
 */

public class OrderComparator implements Comparator<OrderItem> {
    @Override
    public int compare(OrderItem o1, OrderItem o2) {
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
