package com.edu.schooltask.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.GetMoneyEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.http.HttpCheckToken;
import com.edu.schooltask.http.HttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import static android.view.View.GONE;

public class MoneyActivity extends BaseActivity {
    private TextView moneyText;
    private TextView rechargeBtn;
    private TextView pushMoneyBtn;
    private ProgressBar progressBar;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        moneyText = (TextView) findViewById(R.id.money_money);
        rechargeBtn = (TextView) findViewById(R.id.money_recharge);
        pushMoneyBtn = (TextView) findViewById(R.id.money_push_money);
        progressBar = (ProgressBar) findViewById(R.id.money_pro);
        getMoney();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMoney(GetMoneyEvent event) throws JSONException {
        progressBar.setVisibility(GONE);
        if (event.isOk()){
            float money = (float)event.getData().getDouble("money");
            moneyText.setText(money+"");
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event) throws JSONException {
        finish();
    }

    private void getMoney(){
        User user = mDataCache.getUser();
        if(user != null){
            HttpUtil.getMoney(user.getToken(), user.getUserId());
        }
    }
}
