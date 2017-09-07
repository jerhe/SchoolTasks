package com.edu.schooltask.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;
import c.b.QListener;
import server.api.event.user.account.GetMoneyEvent;
import server.api.SchoolTask;
import server.api.event.user.account.RechargeEvent;

import static android.view.View.GONE;
import static com.edu.schooltask.utils.StringUtil.moneyFormat;

public class MoneyActivity extends BaseActivity {
    @BindView(R.id.money_money) TextView moneyText;
    //@BindView(R.id.money_recharge) TextView rechargeBtn;
    //@BindView(R.id.money_push_money) TextView pushMoneyBtn;
    @BindView(R.id.money_pro) ProgressBar progressBar;

    @OnClick(R.id.money_recharge)
    public void recharge(){
        BP.init("b8423903660c0d5aa0f0bcee7af3fb09");
        DialogUtil.createRechargeDialog(MoneyActivity.this, new DialogUtil.RechargeListener() {
            @Override
            public void onRecharge(final BigDecimal money, final String type) {
                BP.pay("微任务", "微任务充值 " + money + " 元", money.doubleValue(), type.equals("支付宝"), new PListener() {
                    @Override
                    public void orderId(String s) {
                        orderId = s;
                    }
                    @Override
                    public void succeed() {
                        BP.query(orderId, new QListener() {
                            @Override
                            public void succeed(String s) {
                                SchoolTask.recharge(orderId,money,type);
                            }
                            @Override
                            public void fail(int i, String s) {
                                toastShort("支付失败：" + i);
                            }
                        });
                    }

                    @Override
                    public void fail(int i, String s) {
                        switch (i){
                            case 6001:
                                toastShort("支付取消");
                                break;
                            case -5:
                                toastShort("请先安装支付插件");
                                break;
                            default:
                                toastShort("支付失败：" + i);
                        }
                    }

                    @Override
                    public void unknow() {
                        toastShort("支付失败");
                    }
                });
            }
        }).show();
    }
    @OnClick(R.id.money_transfer)
    public void transfer(){

    }

    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getMoney();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMoney(GetMoneyEvent event) {
        progressBar.setVisibility(GONE);
        if (event.isOk()){
            String money = (String)event.getData();
            moneyText.setText(moneyFormat(money));
        }
        else{
            moneyText.setText(getString(R.string.getError));
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecharge(RechargeEvent event){
        if(event.isOk()){
            getMoney();
        }
        else{
            toastShort(getString(R.string.getPayInfoError));
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
