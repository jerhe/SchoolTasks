package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FunctionAdapter;
import item.FunctionItem;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends Fragment {
    private View view;
    private RecyclerView userFunctionRecyclerView;
    private List<FunctionItem> userFunctionItemList = new ArrayList<>();
    private RecyclerView systemFunctionRecyclerView;
    private List<FunctionItem> systemFunctionItemList = new ArrayList<>();
    private LinearLayout backgroundLayout;
    public UserFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_user_page,container,false);
            init();
        }
        return view;
    }

    private void init(){
        userFunctionRecyclerView = (RecyclerView)view.findViewById(R.id.up_rv);
        FunctionAdapter userFunctionAdapter = new FunctionAdapter(R.layout.item_user_function, userFunctionItemList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4,1);
        userFunctionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        userFunctionRecyclerView.setAdapter(userFunctionAdapter);
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "个人账户"));
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


        backgroundLayout = (LinearLayout) view.findViewById(R.id.uf_bg);
        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
