package listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class BannerViewPagerPointer implements ViewPager.OnPageChangeListener {
    private Context context;
    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private int size;
    private int imgSize = 20;
    public boolean isDraging = false;
    private List<ImageView> dotViewLists = new ArrayList<>();

    public BannerViewPagerPointer(Context context, ViewPager viewPager, LinearLayout dotLayout, int size) {
        this.context = context;
        this.viewPager = viewPager;
        this.dotLayout = dotLayout;
        this.size = size;

        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //为小圆点左右添加间距
            params.leftMargin = 10;
            params.rightMargin = 10;
            //手动给小圆点一个大小
            params.height = imgSize;
            params.width = imgSize;
            if (i == 0) {   //初始化选中第一个
                imageView.setBackgroundResource(R.drawable.ic_icon_pointer_light);
            } else {
                imageView.setBackgroundResource(R.drawable.ic_icon_pointer);
            }
            //为LinearLayout添加ImageView
            dotLayout.addView(imageView, params);
            dotViewLists.add(imageView);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < size; i++) {
            //选中的页面改变小圆点为选中状态，反之为未选中
            if ((position % size) == i) {
                ((View) dotViewLists.get(i)).setBackgroundResource(R.drawable.ic_icon_pointer_light);
            } else {
                ((View) dotViewLists.get(i)).setBackgroundResource(R.drawable.ic_icon_pointer);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {   //滚动状态 用于判断用户是否正在拖动
        switch (state){
            case ViewPager.SCROLL_STATE_DRAGGING:
                isDraging = true;
                break;
            default:
                isDraging = false;
        }
    }
}
