package com.edu.schooltask.activity;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.DynamicAdapter;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.event.DeleteDynamicEvent;
import com.edu.schooltask.event.LikeChangedEvent;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.MenuRecyclerView;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.DynamicClient;
import server.api.result.Result;

public class DynamicListActivity extends BaseActivity {

    @BindView(R.id.dl_type) ImageTextView typeButton;

    @OnClick(R.id.dl_type)
    public void type(){
        typeButton.animate().rotation(180);
        typeMenu.showAsDropDown(typeButton, 0, DensityUtil.dipToPx(this, 10));
    }

    private RefreshPageRecyclerView<Dynamic> recyclerView;
    private PopupWindow typeMenu;
    private MenuRecyclerView menuRecyclerView;
    private int type;   //0：所有 1：我的学校 2：好友

    @Override
    public int getLayout() {
        return R.layout.activity_dynamic_list;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        typeMenu = new PopupWindow(
                DensityUtil.dipToPx(this, 100),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        typeMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                typeButton.animate().rotation(360);
            }
        });
        menuRecyclerView = DialogUtil.createPopupMenu(this, typeMenu, new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                typeMenu.dismiss();
                int oldType = type;
                switch (position){
                    case 1:
                    case 2:
                        if(!UserUtil.hasLogin()){
                            EventBus.getDefault().post(new UnloginEvent());
                            break;
                        }
                    default:
                        type = position;
                }
                if(type != oldType) {
                    typeButton.setText(menuRecyclerView.get(position));
                    recyclerView.refresh();
                }
            }
        });
        setMenuItems("所有", "我的学校", "好友");

        recyclerView = new RefreshPageRecyclerView<Dynamic>(this, false) {
            @Override
            public void requestPageData(Result result, int page) {
                DynamicClient.getDynamicList(result, type, page);
            }

            @Override
            protected BaseQuickAdapter initAdapter(List<Dynamic> list) {
                return new DynamicAdapter(list);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<Dynamic> list = GsonUtil.toDynamicList(data);
                recyclerView.add(list.getList(), list.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, Dynamic dynamic) {
                openActivity(DynamicActivity.class, "dynamic", dynamic);
            }
        };
        recyclerView.setEmptyView(R.layout.empty_dynamic);
        recyclerView.refresh();
        addView(recyclerView);
    }

    private void setMenuItems(String...items){
        menuRecyclerView.clear();
        for (String item : items) menuRecyclerView.add(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeChanged(LikeChangedEvent event){
        List<Dynamic> list = recyclerView.get();
        for(Dynamic dynamic : list){
            if(dynamic.getId() == event.getId()){
                dynamic.setLike(event.isLike());
                dynamic.setLikeCount(dynamic.getLikeCount() + (event.isLike() ? 1 : -1));
                recyclerView.update(list.indexOf(dynamic));
                return;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteDynamic(DeleteDynamicEvent event){
        List<Dynamic> list = recyclerView.get();
        Dynamic dynamic = null;
        for(Dynamic d : list){
            if(d.getId() == event.getDynamicId()) {
                dynamic = d;
                break;
            }
        }
        if(dynamic != null) {
            list.remove(dynamic);
            recyclerView.notifyDataSetChanged();
        }
    }
}
