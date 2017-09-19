package com.edu.schooltask.activity;

import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.TaskItemAdapter;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.edu.schooltask.view.task.TaskFilterView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import server.api.client.TaskClient;
import server.api.result.Result;

public class TaskListActivity extends BaseActivity {

    @BindView(R.id.tl_search_text) EditText searchText;
    @BindView(R.id.tl_tfv) TaskFilterView taskFilterView;

    @OnClick(R.id.tl_tf_btn)
    public void textFilter(){
        if(taskFilterView.isShown()) taskFilterView.hide();
        else taskFilterView.show();

    }

    @OnTextChanged(R.id.tl_search_text)
    public void searchTextChanged(){
        recyclerView.refresh();
    }

    private RefreshPageRecyclerView<TaskItem> recyclerView;

    Animation searchLayoutInAnimation;
    Animation searchLayoutOutAnimation;

    String school;
    String des;
    String search;
    BigDecimal minCost = new BigDecimal(0);
    BigDecimal maxCost = new BigDecimal(0);


    @Override
    public int getLayout() {
        return R.layout.activity_task_list;
    }

    @Override
    public void init(){
        searchLayoutInAnimation = AnimationUtils.loadAnimation(TaskListActivity.this ,
                R.anim.translate_top_in);
        searchLayoutOutAnimation = AnimationUtils.loadAnimation(TaskListActivity.this ,
                R.anim.translate_top_out);
        UserInfoWithToken user = UserUtil.getLoginUser();
        taskFilterView.addFilter(getString(R.string.school), "所有学校", user == null ? null : user.getSchool());
        taskFilterView.addFilter(getString(R.string.description), getResources().getStringArray(R.array.taskDescription));
        taskFilterView.addFilter(getString(R.string.reward), getResources().getStringArray(R.array.taskReward));
        taskFilterView.setFilterChangeListener(new TaskFilterView.FilterChangeListener() {
            @Override
            public void onFilterChange() {
                recyclerView.refresh();
            }
        });
        searchText.setHintTextColor(Color.GRAY);

        recyclerView = new RefreshPageRecyclerView<TaskItem>(this, false) {
            @Override
            protected BaseQuickAdapter initAdapter(List<TaskItem> list) {
                return new TaskItemAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                getTaskList(result, page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<TaskItem> list = GsonUtil.toTaskItemList(data);
                recyclerView.add(list.getList(), list.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, TaskItem taskItem) {
                openActivity(WaitAcceptOrderActivity.class, "taskItem", taskItem);
            }
        };
        recyclerView.setEmptyView(R.layout.empty_task);
        addView(recyclerView);
        recyclerView.refresh();
    }

    private void getTaskList(Result result, int page){
        String school = taskFilterView.getValue("学校");
        if("所有学校".equals(school)) school = "";
        String des = taskFilterView.getValue("分类");
        if("所有".equals(des)) des = "";
        String search = searchText.getText().toString();
        int costIndex = taskFilterView.getSelectedIndex("报酬");
        BigDecimal minCost;
        BigDecimal maxCost;
        switch (costIndex){
            case 0:
                minCost = new BigDecimal(0);
                maxCost = new BigDecimal(10000);
                break;
            case 1:
                minCost = new BigDecimal(1);
                maxCost = new BigDecimal(5);
                break;
            case 2:
                minCost = new BigDecimal(5);
                maxCost = new BigDecimal(10);
                break;
            case 3:
                minCost = new BigDecimal(10);
                maxCost = new BigDecimal(20);
                break;
            case 4:
                minCost = new BigDecimal(20);
                maxCost = new BigDecimal(100);
                break;
            case 5:
                minCost = new BigDecimal(100);
                maxCost = new BigDecimal(10000);
                break;
            default:
                minCost = new BigDecimal(0);
                maxCost = new BigDecimal(10000);
        }
        //赋值
        if(!(school.equals(this.school) && des.equals(this.des) && search.equals(this.search)
        && minCost.compareTo(this.minCost) == 0 && maxCost.compareTo(this.maxCost) == 0)) {
            recyclerView.clear();
            page = 0;
            this.school = school;
            this.des = des;
            this.search = search;
            this.minCost = minCost;
            this.maxCost = maxCost;
        }
        TaskClient.searchTask(result, school, des, search, minCost, maxCost, page);
    }

}
