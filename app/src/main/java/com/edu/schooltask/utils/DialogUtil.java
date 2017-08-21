package com.edu.schooltask.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.SetPayPwdActivity;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.ui.view.TaskContentView;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;

import server.api.base.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/15.
 */

public class DialogUtil {

    public static DialogPlus createTextDialog(Context context, String title,
                                              String content, String hint, String yesText,
                                              final OnClickListener listener, String noText){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_text))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.dt_ok:
                                dialog.dismiss();
                                listener.onClick(dialog);
                                break;
                            case R.id.dt_cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_title);
        TextView contentText = (TextView) dialogView.findViewById(R.id.dt_content);
        TextView hintText = (TextView) dialogView.findViewById(R.id.dt_hint);
        TextView yesBtn = (TextView) dialogView.findViewById(R.id.dt_ok);
        TextView noBtn = (TextView) dialogView.findViewById(R.id.dt_cancel);
        titleText.setText(title);
        contentText.setText(content);
        if(hint.length() == 0) hintText.setVisibility(View.GONE);
        else hintText.setText(hint);
        yesBtn.setText(yesText);
        noBtn.setText(noText);
        return dialog;
    }

    public static DialogPlus createPayDialog(final BaseActivity activity, final OnPayListener listener, String cost, final BaseTokenEvent event){
        int margin = (int)activity.getResources().getDimension(R.dimen.dialog_margin);
        final DialogPlus payDialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.dialog_pay))
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setMargin(margin,0,margin,0)
                .setOutAnimation(R.anim.dialog_out)
                .setCancelable(false)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.pwd_confirm_btn:
                                View dialogView = dialog.getHolderView();
                                InputTextView pwdText = (InputTextView) dialogView.findViewById(R.id.pwd_pwd);
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
        InputTextView pwdText = (InputTextView) payDialog.findViewById(R.id.pwd_pwd);
        pwdText.setInputFilter(new NumberFilter());
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
        void onClick(DialogPlus dialogPlus);
    }

    public static DialogPlus createListDialog(Context context, BaseAdapter adapter, OnItemClickListener listener){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ListHolder())
                .setMargin(margin,0,margin,0)
                .setAdapter(adapter)
                .setOnItemClickListener(listener)
                .create();
        return dialog;
    }

    public interface OnInputClickListener{
        void onInputClick(DialogPlus dialogPlus, String input);
    }

    public static DialogPlus createInputDialog(Context context, final OnInputClickListener listener,
                                               String title, String hint){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_input))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        EditText inputText = (EditText) dialog.getHolderView().findViewById(R.id.di_input);
                        switch (view.getId()){
                            case R.id.di_yes:
                                listener.onInputClick(dialog, inputText.getText().toString());
                                break;
                            case R.id.di_no:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TextView titleText = (TextView) dialogView.findViewById(R.id.di_title);
        titleText.setText(title);
        EditText inputText = (EditText) dialogView.findViewById(R.id.di_input);
        inputText.setHint(hint);
        return dialog;
    }

    public static DialogPlus createInputMultilineDialog(Context context, final OnInputClickListener listener,
                                               String title, String defaultContent){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_input_multiline))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        TaskContentView inputText = (TaskContentView) dialog.getHolderView().findViewById(R.id.dim_content);
                        switch (view.getId()){
                            case R.id.dim_yes:
                                listener.onInputClick(dialog, inputText.getText().toString());
                                break;
                            case R.id.dim_no:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TextView titleText = (TextView) dialogView.findViewById(R.id.dim_title);
        titleText.setText(title);
        TaskContentView taskContentView = (TaskContentView) dialogView.findViewById(R.id.dim_content);
        taskContentView.setText(defaultContent);
        return dialog;
    }

    public interface DatePickerListener{
        void onDatePicker(DialogPlus dialogPlus, String date);
    }


    public static DialogPlus createHeadImageDialog(Context context, String userId){
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_trans)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_image))
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        GestureImageView imageView = (GestureImageView) dialogView.findViewById(R.id.image_image);
        GlideUtil.setHead(context, userId, imageView);
        return dialog;
    }

    public interface RechargeListener{
        void onRecharge(BigDecimal money, String type);
    }

    public static DialogPlus createRechargeDialog(final BaseActivity activity, final RechargeListener listener){
        int margin = (int)activity.getResources().getDimension(R.dimen.dialog_margin);
        final DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_recharge))
                .setMargin(margin,0,margin,0)
                .create();
        View dialogView = dialog.getHolderView();
        final InputTextView inputTextView = (InputTextView) dialogView.findViewById(R.id.recharge_money);
        inputTextView.setInputFilter(new MoneyFilter());
        final MaterialSpinner typeSpinner = (MaterialSpinner) dialogView.findViewById(R.id.recharge_type);
        typeSpinner.setItems("支付宝", "微信");
        Button button = (Button) dialogView.findViewById(R.id.recharge_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = inputTextView.getText();
                if(money.length() == 0){
                    activity.toastShort("请输入充值金额");
                    return;
                }
                if(!StringUtil.isMoney(money)){
                    activity.toastShort("充值金额错误,请重新输入");
                    return;
                }
                String type = typeSpinner.getText().toString();
                listener.onRecharge(new BigDecimal(money), type);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
