package com.edu.schooltask.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.view.recyclerview.TextRecyclerView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskCountView extends RelativeLayout {
    private TextView commentCountText;
    private TextView lookCountText;
    private TextView commentOrderText;

    private OnOrderChangedListener orderChangedListener;
    private PopupWindow orderMenu;

    int commentCount = 0;

    public TaskCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_count, this);

        commentCountText = (TextView) findViewById(R.id.tc_comment_count);
        lookCountText = (TextView) findViewById(R.id.tc_look_count);
        commentOrderText = (TextView) findViewById(R.id.tc_order);
        orderMenu = new PopupWindow(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final TextRecyclerView recyclerView = new TextRecyclerView(context, null);
        recyclerView.setBackgroundResource(R.drawable.bg_text_list);
        recyclerView.add("按倒序");
        recyclerView.add("按正序");
        recyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                orderMenu.dismiss();
                String selected = recyclerView.get(position);
                if(!commentOrderText.getText().toString().equals(selected)){
                    commentOrderText.setText(selected);
                    if(orderChangedListener != null) orderChangedListener.onOrderChanged(selected);
                }
            }
        });
        orderMenu.setContentView(recyclerView);
        orderMenu.setOutsideTouchable(true);
        commentOrderText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                orderMenu.showAsDropDown((View)getParent(), getWidth() - commentOrderText.getWidth()*3/2, 10);
            }
        });
    }

    public void setCommentCount(int count){
        this.commentCount = count;
        commentCountText.setText("评论 " + count);
    }

    public void setLookCount(int count){
        lookCountText.setText("浏览 " + count);
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
