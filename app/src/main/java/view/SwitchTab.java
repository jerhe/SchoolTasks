package view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class SwitchTab extends LinearLayout implements View.OnClickListener{
    public final static int PAGE_ALL_ORDER = 0;
    public final static int PAGE_WAIT_ASSESS = 1;

    public final static String NORMAL_COLOR = "#757575";
    public final static String LIGHT_COLOR = "#1B9DFF";


    private OnMenuSelectedListener onMenuSelectedListener;

    private TextView allOrderTab;
    private TextView waitAssessTab;

    private View pointer;


    public SwitchTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout.inflate(context,R.layout.view_switch_tab,this);
        allOrderTab = (TextView) findViewById(R.id.st_all_order);
        waitAssessTab = (TextView) findViewById(R.id.st_wait_assess);
        pointer = findViewById(R.id.st_pointer);
        allOrderTab.setOnClickListener(this);
        waitAssessTab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(onMenuSelectedListener != null){
            setLight(v.getId());
        }
    }

    /**
     * setIcon 设置文本颜色，高亮和默认
     * @param lightViewId 高亮的菜单ID
     */
    public void setLight(int lightViewId){
        switch (lightViewId){
            case R.id.st_all_order:
                onMenuSelectedListener.onMenuSelected(PAGE_ALL_ORDER);
                allOrderTab.setTextColor(Color.parseColor(LIGHT_COLOR));
                waitAssessTab.setTextColor(Color.parseColor(NORMAL_COLOR));
                break;
            case R.id.st_wait_assess:
                onMenuSelectedListener.onMenuSelected(PAGE_WAIT_ASSESS);
                allOrderTab.setTextColor(Color.parseColor(NORMAL_COLOR));
                waitAssessTab.setTextColor(Color.parseColor(LIGHT_COLOR));
                break;
        }
    }

    /**
     * setOnMenuSelectedListener 设置菜单切换事件监听器
     * @param onMenuSelectedListener
     */
    public void setOnMenuSelectedListener(OnMenuSelectedListener onMenuSelectedListener){
        this.onMenuSelectedListener = onMenuSelectedListener;
    }

    /**
     * setPagePosition 设置当前页面序号,用于高亮图标
     * @param position
     *
     */
    public void setPagePosition(int position){
        switch (position){
            case 0:
                setLight(R.id.st_all_order);
                break;
            case 1:
                setLight(R.id.st_wait_assess);
                break;
        }
    }

    /**
     * setPointerPosition 设置指示器位置,当viewpager发生滚动时调用
     * @param position  当前页面序号
     * @param offset    滚动偏移百分比
     */
    public void setPointerPosition(int position, float offset){
        int width = pointer.getWidth();
        pointer.setX(width * position + width * offset);
    }

    /**
     * OnMenuSelectedListener 菜单切换事件接口
     */
    public interface OnMenuSelectedListener{
        void onMenuSelected(int position);
    }

}
