package com.edu.schooltask.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.activity.SetPayPwdActivity;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.InputText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/15.
 */

public class DialogUtil {

    public static DialogPlus createYesNoDialog(Context context, String title,
                                               String content, String hint, String yesText,
                                               final OnClickListener listener, String noText){
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.shape_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_yesno))
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.yes_no_yes:
                                dialog.dismiss();
                                listener.onClick();
                                break;
                            case R.id.yes_no_no:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TextView titleText = (TextView) dialogView.findViewById(R.id.yes_no_title);
        TextView contentText = (TextView) dialogView.findViewById(R.id.yes_no_content);
        TextView hintText = (TextView) dialogView.findViewById(R.id.yes_no_hint);
        TextView yesBtn = (TextView) dialogView.findViewById(R.id.yes_no_yes);
        TextView noBtn = (TextView) dialogView.findViewById(R.id.yes_no_no);
        titleText.setText(title);
        contentText.setText(content);
        hintText.setText(hint);
        yesBtn.setText(yesText);
        noBtn.setText(noText);
        return dialog;
    }

    public static DialogPlus createPayDialog(final BaseActivity activity, final OnPayListener listener, String cost, final BaseTokenEvent event){
        final DialogPlus payDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_pay))
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.drawable.shape_dialog)
                .setOutAnimation(R.anim.dialog_out)
                .setCancelable(false)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.pwd_confirm_btn:
                                View dialogView = dialog.getHolderView();
                                InputText pwdText = (InputText) dialogView.findViewById(R.id.pwd_pwd);
                                String pwd = pwdText.getText();
                                if(pwd.length() == 0){
                                    activity.toastShort("请输入支付密码");
                                    return;
                                }
                                if(pwd.length() != 6){
                                    activity.toastShort("支付密码为6位数字");
                                    return;
                                }
                                dialog.dismiss();
                                listener.onPay(pwd);
                                break;
                        }
                    }
                })
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                        EventBus.getDefault().post(event.setError("取消支付"));
                    }
                })
                .create();
        LinearLayout moneyLayout = (LinearLayout) payDialog.findViewById(R.id.pay_money_layout);
        if(null == cost){
            moneyLayout.setVisibility(View.GONE);
        }
        else{
            TextView moneyText = (TextView) payDialog.findViewById(R.id.pay_money);
            moneyText.setText(cost + "元");
        }
        InputText pwdText = (InputText) payDialog.findViewById(R.id.pwd_pwd);
        pwdText.setInputFilter(5);
        TextView setPayPwdText = (TextView) payDialog.findViewById(R.id.pay_set_pwd);
        setPayPwdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openActivity(SetPayPwdActivity.class);
            }
        });
        return payDialog;
    }

    public interface OnPayListener{
        void onPay(String pwd);
    }

    public interface OnClickListener{
        void onClick();
    }
}
