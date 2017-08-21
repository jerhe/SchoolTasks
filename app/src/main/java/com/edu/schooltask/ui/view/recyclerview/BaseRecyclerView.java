package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public abstract class BaseRecyclerView<T> extends RecyclerView {
    protected List<T> list = new ArrayList<>();
    protected BaseQuickAdapter adapter;
    private OnGetDataListener getDataListener;

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(new LinearLayoutManager(context));
        adapter = initAdapter(list);
        adapter.openLoadAnimation();
        setAdapter(adapter);
    }

    //禁止在该方法里面设置空布局setEmptyView
    protected abstract BaseQuickAdapter initAdapter(List<T> list);

    //-----------------------------数据处理-------------------------------

    public void add(T t){
        list.add(t);
        adapter.notifyDataSetChanged();
    }

    public void add(List<T> list){
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public T get(int position){
        return list.get(position);
    }

    public List<T> get(){
        return list;
    }

    public void remove(int position){
        list.remove(position);
    }

    public void removeLast(){
        list.remove(list.size() - 1);
    }

    public void clear(boolean update){
        list.clear();
        if(update) adapter.notifyDataSetChanged();
    }

    public void setEmptyView(int resId){
        adapter.bindToRecyclerView(this);
        adapter.setEmptyView(resId);
    }

    //--------------------------------------------------------------------

    //获取数据接口
    public interface OnGetDataListener{
        void onGetData();
    }

    public void setOnGetDataListener(OnGetDataListener listener){
        this.getDataListener = listener;
    }

    public void refresh(){
        if(getDataListener == null) throw new RuntimeException("未设置获取数据接口");
        clear(false);
        getDataListener.onGetData();
    }

    //添加头部
    public void addHeader(View view){
        adapter.addHeaderView(view);
    }

    //添加尾部
    public void addFooter(View view) {
        adapter.addFooterView(view);
    }

    //点击项事件
    public void setOnItemClickListener(BaseQuickAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
    }
}
