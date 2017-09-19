package com.edu.schooltask.view.recyclerview;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.edu.schooltask.R;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/9/20.
 */

public class HeadRecyclerView extends BaseRecyclerView<String>{

    public HeadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false));
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<String> list) {
        return new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_head, list) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                CircleImageView imageView = helper.getView(R.id.head);
                if(StringUtil.isEmpty(item))
                    UserUtil.setHead(imageView.getContext(), R.drawable.ic_default_head, imageView);
                else UserUtil.setHead(imageView.getContext(), item, imageView);
            }
        };
    }

    @Override
    protected void init() {
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
    }
}
