package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.view.listener.ItemChildClickListener;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.listener.ItemLongClickListener;

import java.util.List;

import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/9/16.
 */

public abstract class RefreshRecyclerView<T> extends PullRefreshLayout {

    protected BaseRecyclerView<T> recyclerView;
    protected Result result;

    public RefreshRecyclerView(Context context, boolean withToken) {
        super(context, null);
        result = new Result(withToken) {
            @Override
            public void onResponse(int id) {
                setRefreshing(false);
            }

            @Override
            public void onSuccess(int id, Object data) {
                RefreshRecyclerView.this.onSuccess(id, data);
            }

            @Override
            public void onFailed(int id, int code, String error) {
                RefreshRecyclerView.this.onFailed(id, code, error);
            }
        };

        setColor(getResources().getColor(R.color.colorPrimary));
        initView(context);
    }

    /**
     * 初始化布局
     * @param context
     */
    protected void initView(Context context){
        recyclerView =  new BaseRecyclerView<T>(context, null) {
            @Override
            protected BaseQuickAdapter initAdapter(List list) {
                return RefreshRecyclerView.this.initAdapter(list);
            }

            @Override
            protected void init() {
                adapter.openLoadAnimation();
                adapter.isFirstOnly(false);
            }
        };
        recyclerView.setOnRequestDataListener(new BaseRecyclerView.OnRequestDataListener() {
            @Override
            public void onRequestData() {
                RefreshRecyclerView.this.requestData(result);
            }
        });
        recyclerView.setItemClickListener(new ItemClickListener<T>() {
            @Override
            public void onItemClick(int position, T t) {
                RefreshRecyclerView.this.onItemClick(position, t);
            }
        });
        recyclerView.setItemChildClickListener(new ItemChildClickListener<T>() {
            @Override
            public void onItemChildClick(View view, int position, T t) {
                onChildClick(view, position, t);
            }
        });
        recyclerView.setItemLongClickListener(new ItemLongClickListener<T>() {
            @Override
            public void onItemLongClick(int position, T t) {
                RefreshRecyclerView.this.onItemLongClick(position, t);
            }
        });
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing();
                recyclerView.refresh();
            }
        });
        addView(recyclerView);
    }

    /**
     * 初始化适配器
     * @param list
     * @return
     */
    protected abstract BaseQuickAdapter initAdapter(List<T> list);

    /**
     * 请求数据接口
     * @param result
     */
    protected abstract void requestData(Result result);

    /**
     * 正在请求
     */
    protected void onRefreshing(){}

    /**
     * 请求成功回调
     * @param id
     * @param data
     */
    protected abstract void onSuccess(int id, Object data);

    /**
     * 请求失败回调
     * @param id
     * @param code
     * @param error
     */
    protected abstract void onFailed(int id, int code, String error);

    /**
     * 子项点击事件
     * @param position
     * @param t
     */
    protected void onItemClick(int position, T t){}

    /**
     * 子项子视图点击事件
     * @param view
     * @param position
     * @param t
     */
    protected void onChildClick(View view, int position, T t){}

    /**
     * 子项长按事件
     */
    protected void onItemLongClick(int position, T t){}

    /**
     * 刷新
     */
    public void refresh(){
        onRefreshing();
        setRefreshing(true);
        recyclerView.refresh();
    }

    /**
     * 获取数据
     * @return
     */
    public List<T> get(){
        return recyclerView.get();
    }

    public void add(T t){
        recyclerView.add(t);
    }

    public void add(List<T> list){
        recyclerView.add(list);
    }

    /**
     * 设置空布局
     * @param resId
     */
    public void setEmptyView(int resId){
        recyclerView.setEmptyView(resId);
    }

    public void notifyDataSetChanged(){
        recyclerView.notifyDataSetChanged();
    }

    /**
     * 清空
     */
    public void clear(){
        recyclerView.clear();
    }

    public void addFooter(View view){
        recyclerView.addFooter(view);
    }

}
