package com.edu.schooltask.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.InputText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;
import server.api.account.GetMoneyEvent;
import server.api.SchoolTask;
import server.api.account.RechargeEvent;

import static android.view.View.GONE;

public class MoneyActivity extends BaseActivity {
    private TextView moneyText;
    private TextView rechargeBtn;
    private TextView pushMoneyBtn;
    private ProgressBar progressBar;

    String orderId;
    BigDecimal money;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        EventBus.getDefault().register(this);
        moneyText = getView(R.id.money_money);
        rechargeBtn = getView(R.id.money_recharge);
        pushMoneyBtn = getView(R.id.money_push_money);
        progressBar = getView(R.id.money_pro);

        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
    public void onRecharge(RechargeEvent event){
        if(event.isOk()){
            getMoney();
        }
        else{
            toastShort("获取支付信息失败，如未到账请联系客服");
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
