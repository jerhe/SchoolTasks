package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.view.listener.ItemChildClickListener;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.listener.ItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public abstract class BaseRecyclerView<T> extends RecyclerView {
    protected List<T> list = new ArrayList<>();
    protected BaseQuickAdapter adapter;
    protected boolean isRefreshing;
    private ItemClickListener<T> itemClickListener;
    private ItemLongClickListener<T> itemLongClickListener;
    private ItemChildClickListener<T> itemChildClickListener;
    private OnRequestDataListener requestDataListener;


    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(new LinearLayoutManager(context));
        adapter = initAdapter(list);
        setAdapter(adapter);
        adapter.bindToRecyclerView(this);
        setOverScrollMode(OVER_SCROLL_NEVER);
        init();
    }

    protected abstract BaseQuickAdapter initAdapter(List<T> list);

    protected void init(){}

    //-----------------------------数据处理-------------------------------

    public void add(T t){
        list.add(t);
        adapter.notifyDataSetChanged();
    }

    public void add(int index, T t){
        list.add(index, t);
        adapter.notifyDataSetChanged();
    }

    public void add(List<T> list){
        List dataList = adapter.getData();
        for(T t : list){    //不添加重复数据
            if(dataList.indexOf(t) == -1) adapter.addData(t);
        }
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

    public void update(int index){
        adapter.notifyItemChanged(index);
    }

    public void clear(){
        list.clear();
        adapter.notifyDataSetChanged();
    }

    public void setEmptyView(int resId){
        adapter.setEmptyView(resId);
    }

    public void removeAllHeader(){
        adapter.removeAllHeaderView();
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    //--------------------------------------------------------------------

    //获取数据接口
    public interface OnRequestDataListener {
        void onRequestData();
    }

    public void setOnRequestDataListener(OnRequestDataListener listener){
        this.requestDataListener = listener;
    }

    public void refresh(){
        if(requestDataListener == null) throw new RuntimeException("未设置获取数据接口");
        clear();
        requestDataListener.onRequestData();
    }

    //添加头部
    public void addHeader(View view){
        adapter.addHeaderView(view);
    }

    //设置头部和空布局同时显示
    public void setHeaderAndEmpty(boolean b){
        adapter.setHeaderAndEmpty(b);
    }

    //添加尾部
    public void addFooter(View view) {
        adapter.addFooterView(view);
    }

    public void setItemClickListener(ItemClickListener<T> listener){
        this.itemClickListener = listener;
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(itemClickListener != null) itemClickListener.onItemClick(position, get(position));
            }
        });
    }

    public void setItemChildClickListener(ItemChildClickListener<T> listener){
        this.itemChildClickListener = listener;
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(itemChildClickListener != null)
                    itemChildClickListener.onItemChildClick(view, position, get(position));
            }
        });
    }

    public void setItemLongClickListener(ItemLongClickListener<T> listener){
        this.itemLongClickListener = listener;
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if(itemLongClickListener != null){
                    itemLongClickListener.onItemLongClick(position, get(position));
                }
                return true;
            }
        });
    }

}
