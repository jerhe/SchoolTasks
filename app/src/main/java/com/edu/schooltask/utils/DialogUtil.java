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
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.ui.view.TaskContentView;
import com.edu.schooltask.ui.view.InputTextView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.math.BigDecimal;

/**
 * Created by 夜夜通宵 on 2017/5/15.
 */

public class DialogUtil {

    public static DialogPlus createTextDialog(Context context, String title, String content, String hint,
                                              final OnClickListener listener){
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
                                if(listener != null) listener.onClick(dialog);
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
        titleText.setText(title);
        contentText.setText(content);
        if(hint.length() == 0) hintText.setVisibility(View.GONE);
        else hintText.setText(hint);
        return dialog;
    }

    public static DialogPlus createTipDialog(Context context, String title, String content){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_tip))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.dt_ok:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_title);
        TextView contentText = (TextView) dialogView.findViewById(R.id.dt_content);
        titleText.setText(title);
        contentText.setText(content);
        return dialog;
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

    public static DialogPlus createInputSignDialog(Context context, final OnInputClickListener listener,
                                                   String defaultContent){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_input_sign))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        TaskContentView inputText = (TaskContentView) dialog.getHolderView().findViewById(R.id.dis_content);
                        switch (view.getId()){
                            case R.id.dis_yes:
                                listener.onInputClick(dialog, inputText.getText().toString());
                                break;
                            case R.id.dis_no:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TaskContentView taskContentView = (TaskContentView) dialogView.findViewById(R.id.dis_content);
        taskContentView.setText(defaultContent);
        return dialog;
    }

    public static DialogPlus createTaskContentDialog(Context context, final OnInputClickListener listener,
                                                    String defaultContent){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(R.layout.dialog_task_content))
                .setMargin(margin,0,margin,0)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        TaskContentView inputText = (TaskContentView) dialog.getHolderView().findViewById(R.id.dis_content);
                        switch (view.getId()){
                            case R.id.dis_yes:
                                listener.onInputClick(dialog, inputText.getText().toString());
                                break;
                            case R.id.dis_no:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        View dialogView = dialog.getHolderView();
        TaskContentView taskContentView = (TaskContentView) dialogView.findViewById(R.id.dis_content);
        taskContentView.setText(defaultContent);
        return dialog;
    }


    public static DialogPlus createHeadImageDialog(Context context, UserInfo userInfo){
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
        UserUtil.setHead(context, userInfo, imageView);
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
