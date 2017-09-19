package com.edu.schooltask.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.edu.schooltask.R;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.other.DataCache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * Created by 夜夜通宵 on 2017/5/9.
 */

public abstract class BaseFragment extends Fragment {
    protected int redId;
    protected View view;
    protected ViewGroup layout;
    protected static DataCache mDataCache;

    public BaseFragment(){}

    public BaseFragment(int redId){
        this.redId = redId;
        mDataCache = BaseActivity.getDataCache();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            if(redId != 0){
                view = inflater.inflate(redId, container, false);
                layout = getView(R.id.layout);
                ButterKnife.bind(this, view);
                init();
            }
        }
        return view;
    }


    //-----------------------------------------------------------

    protected abstract void init();

    //-----------------------------------------------------------

    //登录事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginEvent event){
        onLogin();
    }

    //登出事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        onLogout();
    }

    protected void onLogin(){}

    protected void onLogout(){}

    protected <T extends View> T getView(int resId){
        return (T) view.findViewById(resId);
    }

    public <T>T inflate(int resId){
        return (T) LayoutInflater.from(getContext()).inflate(resId, null);
    }


    public void addView(View view){
        if(layout != null) layout.addView(view);
    }

    public void openActivity(Class cls){
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    public void openActivity(Class cls, String parameterName, Serializable value){
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(parameterName, value);
        startActivity(intent);
    }

    public void toast(final String text, final int duration){
        ((BaseActivity)getActivity()).toast(text, duration);
    }

    public void toastShort(String text){
        ((BaseActivity)getActivity()).toast(text, Toast.LENGTH_SHORT);
    }

    public void toastLong(String text){
        ((BaseActivity)getActivity()).toast(text, Toast.LENGTH_LONG);
    }

    public void finish(){
        getActivity().finish();
    }
}
