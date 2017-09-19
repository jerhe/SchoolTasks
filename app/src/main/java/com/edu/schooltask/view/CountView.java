package com.edu.schooltask.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.MenuRecyclerView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CountView extends RelativeLayout {
    private TextView commentCountText;
    private TextView lookCountText;
    private TextView commentOrderText;

    private OnOrderChangedListener orderChangedListener;
    private PopupWindow orderMenu;
    private MenuRecyclerView recyclerView;

    private String commentHintText = "评论";
    private String lookHintText = "浏览";
    private int commentCount = 0;

    public CountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_count, this);

        commentCountText = (TextView) findViewById(R.id.count_comment);
        lookCountText = (TextView) findViewById(R.id.count_look);
        commentOrderText = (TextView) findViewById(R.id.count_order);
        orderMenu = new PopupWindow(DensityUtil.dipToPx(context, 120), LayoutParams.WRAP_CONTENT);
        recyclerView = DialogUtil.createPopupMenu(context, orderMenu, new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                orderMenu.dismiss();
                if(!commentOrderText.getText().toString().equals(s)){
                    commentOrderText.setText(s);
                    if(orderChangedListener != null) orderChangedListener.onOrderChanged(s);
                }
            }
        });
        commentOrderText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                orderMenu.showAsDropDown(CountView.this,
                        getWidth() - DensityUtil.dipToPx(getContext(), 125),
                        DensityUtil.dipToPx(getContext(), 3));
            }
        });
    }

    public void setItems(String...items){
        recyclerView.clear();
        for (String item : items) recyclerView.add(item);
    }

    public void setCommentCount(int count){
        this.commentCount = count;
        commentCountText.setText(commentHintText + "\t" + count);
    }

    public void setCommentHint(String text){
        commentHintText = text;
        setCommentCount(commentCount);
    }

    public void hideLookCount(){
        lookCountText.setVisibility(GONE);
    }

    public void setLookCount(int count){
        lookCountText.setText(lookHintText + "\t" + count);
    }

    public void addCommentCount(){
        setCommentCount(commentCount+1);
    }

    public void setCount(int commentCount, int lookCount){
        setCommentCount(commentCount);
        setLookCount(lookCount);
    }

    public String getOrder(){
        return commentOrderText.getText().toString();
    }

    public void setOnOrderChangedListener(OnOrderChangedListener listener){
        this.orderChangedListener = listener;
    }

    public interface OnOrderChangedListener{
        void onOrderChanged(String order);
    }
}
