package com.edu.schooltask.view.listener;

import android.view.View;

/**
 * Created by 夜夜通宵 on 2017/9/18.
 */

public interface ItemChildClickListener<T> {
    void onItemChildClick(View view, int position, T t);
}
