package com.edu.schooltask.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.http.HttpCheckToken;
import com.edu.schooltask.http.HttpResponse;
import com.edu.schooltask.http.HttpUtil;

import static android.view.View.GONE;

public class MoneyActivity extends BaseActivity {
    private TextView moneyText;
    private TextView rechargeBtn;
    private TextView pushMoneyBtn;
    private ProgressBar progressBar;

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

    private void getMoney(){
        HttpUtil.postWithToken(this, user, new HttpCheckToken() {
            @Override
            public void handler() {
                if(isSuccess){
                    HttpUtil.getMoney(user.getUserId(), new HttpResponse() {
                        @Override
                        public void handler() throws Exception {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(GONE);
                                }
                            });
                            switch (code){
                                case 0:
                                    final float money = (float)data.getDouble("money");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            moneyText.setText(money+"");
                                        }
                                    });
                                    break;
                                case 1:
                                    break;
                                default:
                                    toastShort("连接失败,请检查网络");
                            }
                        }
                    });
                }
                else{
                    finish();
                }
            }
        });
    }
}
