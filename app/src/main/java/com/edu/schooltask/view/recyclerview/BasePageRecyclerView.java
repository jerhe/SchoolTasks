package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.view.CustomLoadMoreView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public abstract class BasePageRecyclerView<T> extends BaseRecyclerView<T> {
    private OnRequestPageDataListener requestPageDataListener;
    protected int page = 0;

    public BasePageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        adapter.setEnableLoadMore(true);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                requestPageDataListener.onRequestPageData(page);
            }
        }, this);
    }

    @Override
    public void add(List<T> list) {
        if(page == 0) this.list.clear();
        List dataList = adapter.getData();
        for(T t : list){    //不添加重复数据
            if(dataList.indexOf(t) == -1) adapter.addData(t);
        }
        page++;
        adapter.loadMoreComplete();
        adapter.notifyDataSetChanged();
    }

    public void add(List<T> list, boolean more){
        add(list);
        if(!more) adapter.loadMoreEnd();
    }


    @Override
    public void clear(){
        super.clear();
        page = 0;
    }

    public void loadFailed(){
        adapter.loadMoreFail();
    }

    @Override
    public void refresh() {
        clear();
        if(requestPageDataListener == null) throw new RuntimeException("未设置获取数据接口");
        requestPageDataListener.onRequestPageData(page);
    }


    public interface OnRequestPageDataListener {
        void onRequestPageData(int page);
    }

    public void setOnRequestPageDataListener(OnRequestPageDataListener listener){
        this.requestPageDataListener = listener;
    }
}
