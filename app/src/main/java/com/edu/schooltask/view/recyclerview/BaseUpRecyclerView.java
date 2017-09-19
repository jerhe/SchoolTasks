package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于各种记录上滑加载更多
 * Created by 夜夜通宵 on 2017/8/23.
 */


public abstract class BaseUpRecyclerView<T> extends BaseRecyclerView<T> {
    final static int SHOW_COUNT = 15;    //每次加载的数据量

    List<T> allList = new ArrayList<>();    //所有项
    int offset = -1; //当前加载位置

    public BaseUpRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        adapter.setNotDoAnimationCount(SHOW_COUNT);
    }

    //初始化：加载所有数据，显示部分数据
    public void init(List<T> allList){
        this.allList.addAll(allList);
        initLoad();
        adapter.setUpFetchEnable(true);
        adapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                BaseUpRecyclerView.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upFetch();
                    }
                },300);
            }
        });
    }

    //初始化加载
    private void initLoad(){
        offset = allList.size() - 1;
        upFetch();
        scrollToPosition(list.size() - 1);
    }

    private void upFetch(){
        if(offset < 0) {
            adapter.setUpFetchEnable(false);
            return;
        }
        int endIndex = offset - SHOW_COUNT + 1;
        if(endIndex < 0)  endIndex = 0;
        adapter.addData(0, allList.subList(endIndex, offset + 1));
        offset = endIndex - 1;
        adapter.notifyDataSetChanged();
    }

    //添加新数据
    @Override
    public void add(T t) {
        allList.add(t);
        adapter.addData(list.size(), t);
        adapter.notifyDataSetChanged();
        scrollToPosition(list.size() - 1);
    }

    @Override
    @Deprecated
    public void add(List<T> list) {
        super.add(list);
    }
}
