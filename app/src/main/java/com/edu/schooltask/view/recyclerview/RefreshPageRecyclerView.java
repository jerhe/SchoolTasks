package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.Log;
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

public abstract class RefreshPageRecyclerView<T> extends PullRefreshLayout{

    protected BasePageRecyclerView<T> recyclerView;
    protected Result result;

    public RefreshPageRecyclerView(Context context, boolean withToken) {
        super(context, null);
        result = new Result(withToken) {
            @Override
            public void onResponse(int id) {
                recyclerView.isRefreshing = false;
                setRefreshing(false);
            }

            @Override
            public void onSuccess(int id, Object data) {
                RefreshPageRecyclerView.this.onSuccess(id, data);
            }

            @Override
            public void onFailed(int id, int code, String error) {
                recyclerView.loadFailed();
                RefreshPageRecyclerView.this.onFailed(id, code, error);
            }
        };

        setColor(getResources().getColor(R.color.colorPrimary));
        initView(context);
    }

    protected void initView(Context context) {
        recyclerView = new BasePageRecyclerView<T>(context, null) {
            @Override
            protected BaseQuickAdapter initAdapter(List<T> list) {
                return RefreshPageRecyclerView.this.initAdapter(list);
            }

            @Override
            protected void init() {
                adapter.openLoadAnimation();
                adapter.isFirstOnly(false);
            }
        };
        recyclerView.setOnRequestPageDataListener(new BasePageRecyclerView.OnRequestPageDataListener() {
            @Override
            public void onRequestPageData(int page) {
                RefreshPageRecyclerView.this.requestPageData(result, page);
            }
        });
        recyclerView.setItemClickListener(new ItemClickListener<T>() {
            @Override
            public void onItemClick(int position, T t) {
                RefreshPageRecyclerView.this.onItemClick(position, t);
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
                RefreshPageRecyclerView.this.onItemLongClick(position, t);
            }
        });
        //下拉刷新
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.isRefreshing = true;
                recyclerView.refresh();
                onRefreshing();
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
    protected abstract void requestPageData(Result result, int page);

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
     * 非下拉刷新
     */
    public void refresh(){
        setRefreshing(true);
        recyclerView.refresh();
        onRefreshing();
    }

    /**
     * 获取数据
     * @return
     */
    public List<T> get(){
        return recyclerView.get();
    }

    /**
     * 更新某项视图
     * @param index
     */
    public void update(int index){
        recyclerView.update(index);
    }

    /**
     * 清空
     */
    public void clear(){
        recyclerView.clear();
    }

    /**
     * 设置空布局
     * @param resId
     */
    public void setEmptyView(int resId){
        recyclerView.setEmptyView(resId);
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeader(View view){
        recyclerView.addHeader(view);
    }

    /**
     * 设置头布局和空布局共存
     * @param b
     */
    public void setEmptyAndHeader(boolean b){
        recyclerView.setHeaderAndEmpty(b);
    }

    public void notifyDataSetChanged(){
        recyclerView.notifyDataSetChanged();
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        recyclerView.setBackgroundColor(color);
    }
}
