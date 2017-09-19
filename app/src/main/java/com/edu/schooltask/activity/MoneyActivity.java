package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.TextSpinner;
import com.edu.schooltask.utils.AppUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import server.api.client.AccountClient;
import server.api.result.Result;

import static android.view.View.GONE;
import static com.edu.schooltask.utils.StringUtil.moneyFormat;

public class MoneyActivity extends BaseActivity {
    @BindView(R.id.money_money) TextView moneyText;
    @BindView(R.id.money_recharge_layout) RelativeLayout rechargeLayout;
    @BindView(R.id.money_recharge_ts) TextSpinner rechargeSpinner;
    @BindView(R.id.money_pro) ProgressBar progressBar;

    @OnClick(R.id.money_recharge_btn)
    public void rechargeVisible(){
        if(rechargeLayout.isShown()) rechargeLayout.setVisibility(GONE);
        else {
            rechargeLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.money_recharge)
    public void recharge(){
        progressDialog = ProgressDialog.show(this, "", "加载中", true, true);
        int i = rechargeSpinner.getSelectedIndex();
        int rechargeMoney = 10 + i * i * 10;
        AccountClient.getRechargeCode(getRechargeCodeResult, rechargeMoney);
    }

    @OnClick(R.id.money_transfer)
    public void transfer(){

    }

    @OnClick(R.id.money_alipay)
    public void aliPay(){
        openActivity(AliUserActivity.class);
    }

    private Result getMoneyResult = new Result(true){
        @Override
        public void onResponse(int id) {
            progressBar.setVisibility(GONE);
        }

        @Override
        public void onSuccess(int id, Object data) {
            String money = (String)data;
            moneyText.setText(moneyFormat(money));
        }

        @Override
        public void onFailed(int id, int code, String error) {
            moneyText.setText(getString(R.string.getError));
            toastShort(error);
        }
    };
    private Result getRechargeCodeResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, final Object data) {
            Message message = Message.obtain("admin", Conversation.ConversationType.PRIVATE,
                    new TextMessage("新的充值消息"));
            RongIM.getInstance().sendMessage(message, "", "", new IRongCallback.ISendMediaMessageCallback() {
                @Override
                public void onProgress(Message message, int i) {}
                @Override
                public void onCanceled(Message message) {}
                @Override
                public void onAttached(Message message) {}
                @Override
                public void onSuccess(Message message) {
                    openAlipay((String)data);
                }
                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    Log.e("messageSendErrorCode", errorCode.toString());
                }
            });
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort("获取充值码失败！");
        }
    };
    private ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_money;
    }

    @Override
    public void init() {
        rechargeSpinner.setItems("10元","20元","50元","100元");
        AccountClient.getMoney(getMoneyResult);
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

    public void openAlipay(String code) {
        if (AppUtil.isAvailable(this, "com.eg.android.AlipayGphone")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&qrcode=" + code));
            startActivityForResult(intent, 1);
        } else {
            toastShort("未安装支付宝");
        }
    }
}
