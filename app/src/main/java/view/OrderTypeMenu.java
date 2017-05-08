package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/8.
 */

public class OrderTypeMenu extends LinearLayout implements View.OnClickListener{
    private OnMenuItemSelectedListener onMenuItemSelectedListener;
    private MenuItem allItem;
    private MenuItem waitPayItem;
    private MenuItem waitAcceptItem;
    private MenuItem waitAssessItem;
    private MenuItem assessFinishItem;
    private MenuItem unlessItem;
    private MenuItem cancleItem;
    List<MenuItem> items = new ArrayList<>();

    public OrderTypeMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_order_type_menu,this);
        allItem = (MenuItem) findViewById(R.id.otm_all);
        waitPayItem = (MenuItem) findViewById(R.id.otm_wait_pay);
        waitAcceptItem = (MenuItem) findViewById(R.id.otm_wait_accept);
        waitAssessItem = (MenuItem) findViewById(R.id.otm_wait_assess);
        assessFinishItem = (MenuItem) findViewById(R.id.otm_assess_finish);
        unlessItem = (MenuItem) findViewById(R.id.otm_unless);
        cancleItem = (MenuItem) findViewById(R.id.otm_cancle);
        items.add(allItem);
        items.add(waitPayItem);
        items.add(waitAcceptItem);
        items.add(waitAssessItem);
        items.add(assessFinishItem);
        items.add(unlessItem);
        items.add(cancleItem);
        for(MenuItem menuItem : items){
            menuItem.setOnClickListener(this);
        }
    }

    public void setLight(int id){
        for(MenuItem menuItem : items){
            if(menuItem.getId() == id) {
                menuItem.setLight();
                onMenuItemSelectedListener.OnMenuItemSelected(items.indexOf(menuItem));
            }
            else menuItem.setNormal();
        }
    }

    public void setOnMenuSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener){
        this.onMenuItemSelectedListener = onMenuItemSelectedListener;
        setLight(R.id.otm_all); //默认选中第一个
    }

    @Override
    public void onClick(View v) {
        if(onMenuItemSelectedListener != null){
            setLight(v.getId());
        }
    }

    /**
     * 菜单选择监听接口
     */
    public interface OnMenuItemSelectedListener{
        void OnMenuItemSelected(int position);
    }
}
