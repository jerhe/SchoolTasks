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
    private OnGetPageDataListener getPageDataListener;
    protected int page = 0;


    public BasePageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        adapter.setEnableLoadMore(true);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getPageDataListener.onGetPageData(page);
            }
        }, this);
    }

    @Override
    public void add(List<T> list) {
        if(page == 0) this.list.clear();
        adapter.addData(list);
        page++;
        adapter.loadMoreComplete();
        if(list.size() == 0) adapter.loadMoreEnd();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clear(){
        super.clear();
        page = 0;
    }

    @Override
    public void refresh() {
        clear();
        if(getPageDataListener == null) throw new RuntimeException("未设置获取数据接口");
        getPageDataListener.onGetPageData(page);
    }

    public interface OnGetPageDataListener{
        void onGetPageData(int page);
    }

    public void setOnGetPageDataListener(OnGetPageDataListener listener){
        this.getPageDataListener = listener;
    }
}
