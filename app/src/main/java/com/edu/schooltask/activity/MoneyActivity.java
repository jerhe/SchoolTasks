package com.edu.schooltask.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import server.api.money.GetMoneyEvent;
import server.api.SchoolTask;

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
    public void onGetMoney(GetMoneyEvent event) {
        progressBar.setVisibility(GONE);
        if (event.isOk()){
            String money = event.getMoney();
            int pointIndex = money.lastIndexOf(".");
            StringBuilder sb = new StringBuilder();
            sb.append(money.substring(0,pointIndex));
            sb.reverse();
            for(int i=sb.length()/3; i>0; i--){
                sb.insert(3 * i, ",");
            }
            sb.reverse();
            if(sb.toString().startsWith(","))sb.deleteCharAt(0);
            sb.append(money.substring(pointIndex));
            moneyText.setText(sb.toString());
        }
        else{
            moneyText.setText("获取失败");
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event) throws JSONException {
        finish();
    }

    private void getMoney(){
        SchoolTask.getMoney();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_detail){
            openActivity(DetailActivity.class);
        }
        return true;
    }
}
