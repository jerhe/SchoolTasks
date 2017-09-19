package com.edu.schooltask.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.view.ContentView;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.MenuRecyclerView;
import com.edu.schooltask.view.recyclerview.TextRecyclerView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * Created by 夜夜通宵 on 2017/5/15.
 */

public class DialogUtil {

    public static DialogPlusBuilder createBaseDialogBuilder(Context context, int layoutId){
        int margin = (int)context.getResources().getDimension(R.dimen.dialog_margin);
        return DialogPlus.newDialog(context)
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setGravity(Gravity.CENTER)
                .setOutAnimation(R.anim.dialog_out)
                .setContentHolder(new ViewHolder(layoutId))
                .setMargin(margin,0,margin,0);
    }

    //列表框点击事件接口
    public interface ListItemClickListener{
        void onItemClick(int position, String item);
    }

    //文本列表选择窗
    public static DialogPlus createListDialog(Context context, String title, final ListItemClickListener listener,
                                              String...items){
        final DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_list).create();
        View view = dialog.getHolderView();
        TextView titleText = (TextView) view.findViewById(R.id.dialog_title);
        titleText.setText(title);
        final TextRecyclerView recyclerView = (TextRecyclerView) view.findViewById(R.id.dialog_trv);
        recyclerView.setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                listener.onItemClick(position, s);
                dialog.dismiss();
            }
        });
        for(String item : items) recyclerView.add(item);
        return dialog;
    }

    public interface OnClickListener{
        void onClick(DialogPlus dialogPlus);
    }

    public static DialogPlus createTextDialog(Context context, String title, String content, String hint,
                                              final OnClickListener listener){
        DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_text)
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
        DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_tip)
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


    public interface OnInputClickListener{
        void onInputClick(DialogPlus dialogPlus, String input);
    }

    public static DialogPlus createInputDialog(Context context, final OnInputClickListener listener,
                                               String title, String hint){
        DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_input)
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
        DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_input_sign)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        ContentView inputText = (ContentView) dialog.getHolderView().findViewById(R.id.dis_content);
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
        ContentView contentView = (ContentView) dialogView.findViewById(R.id.dis_content);
        contentView.setText(defaultContent);
        return dialog;
    }

    public static DialogPlus createTaskContentDialog(Context context, final OnInputClickListener listener,
                                                    String defaultContent){
        DialogPlus dialog = createBaseDialogBuilder(context, R.layout.dialog_task_content)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        ContentView inputText = (ContentView) dialog.getHolderView().findViewById(R.id.dis_content);
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
        ContentView contentView = (ContentView) dialogView.findViewById(R.id.dis_content);
        contentView.setText(defaultContent);
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
        UserUtil.setHead(context, userInfo.getHead(), imageView);
        return dialog;
    }

    public static MenuRecyclerView createPopupMenu(Context context, PopupWindow popupWindow,
                                               ItemClickListener<String> listener) {
        MenuRecyclerView recyclerView = new MenuRecyclerView(context, null);
        recyclerView.setBackgroundResource(R.drawable.bg_text_list);
        recyclerView.setItemClickListener(listener);
        popupWindow.setContentView(recyclerView);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        return recyclerView;
    }

}
