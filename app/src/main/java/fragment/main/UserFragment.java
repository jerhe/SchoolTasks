package fragment.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import adapter.FunctionAdapter;
import base.BaseFragment;
import beans.User;
import item.FunctionItem;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends BaseFragment {
    private User user;
    private RecyclerView userFunctionRecyclerView;
    private List<FunctionItem> userFunctionItemList = new ArrayList<>();
    private RecyclerView systemFunctionRecyclerView;
    private List<FunctionItem> systemFunctionItemList = new ArrayList<>();
    private LinearLayout backgroundLayout;

    private TextView nameText;
    private TextView levelText;
    private ImageView headImage;
    public UserFragment(){
        super(R.layout.fragment_user_page);
    }

    @Override
    protected void init(){
        //头像
        headImage = (ImageView) view.findViewById(R.id.up_user_head);
        Glide.with(getContext())
                .load(R.drawable.head)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.head)
                .into(new BitmapImageViewTarget(headImage){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        roundedBitmapDrawable.setCircular(true);
                        headImage.setImageDrawable(roundedBitmapDrawable);
                    }
                });
        nameText = (TextView) view.findViewById(R.id.up_user_name);
        levelText = (TextView) view.findViewById(R.id.up_user_level);
        userFunctionRecyclerView = (RecyclerView)view.findViewById(R.id.up_rv);
        FunctionAdapter userFunctionAdapter = new FunctionAdapter(R.layout.item_user_function, userFunctionItemList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4,1);
        userFunctionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        userFunctionRecyclerView.setAdapter(userFunctionAdapter);
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "寻物启事"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "找兼职"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));

        systemFunctionRecyclerView = (RecyclerView)view.findViewById(R.id.up_rv2);
        FunctionAdapter systemFunctionAdapter = new FunctionAdapter(R.layout.item_system_function, systemFunctionItemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        systemFunctionRecyclerView.setLayoutManager(linearLayoutManager);
        systemFunctionRecyclerView.setAdapter(systemFunctionAdapter);
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "关于"));

        //TEST
        systemFunctionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DataSupport.deleteAll(User.class);
                getActivity().finish();
            }
        });

        backgroundLayout = (LinearLayout) view.findViewById(R.id.uf_bg);
        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
                else{
                    //TODO 跳转到个人主页
                }

            }
        });
    }

    /**
     * 更新该页面User
     * @param user 从主页活动传入User
     */
    public void updateUser(User user){
        this.user = user;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(user != null){
            nameText.setText(user.getName());
            levelText.setVisibility(View.VISIBLE);
            levelText.setText("Lv." + user.getLevel());
        }
        else{
            levelText.setVisibility(View.GONE); //隐藏等级显示
        }
    }
}
