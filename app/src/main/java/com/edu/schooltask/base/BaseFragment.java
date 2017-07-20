package com.edu.schooltask.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 夜夜通宵 on 2017/5/9.
 */

public abstract class BaseFragment extends Fragment {
    protected int redId;
    protected View view;
    protected static DataCache mDataCache;

    public BaseFragment(){}

    public BaseFragment(int redId){
        this.redId = redId;
        mDataCache = BaseActivity.getDataCache();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                init();
            }
        }
        return view;
    }


    //-----------------------------------------------------------

    protected abstract void init();

    //-----------------------------------------------------------

    protected <T extends View> T getView(int resId){
        return (T) view.findViewById(resId);
    }

    public void openActivity(Class cls){
        Intent intent = new Intent(getActivity(), cls);
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
