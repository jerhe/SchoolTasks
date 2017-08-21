package com.edu.schooltask.ui.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/6/2.
 */

public final class CustomLoadMoreView extends LoadMoreView {

    @Override public int getLayoutId() {
        return R.layout.view_load_more;
    }


    @Override protected int getLoadingViewId() {
        return R.id.lm_loading;
    }

    @Override protected int getLoadFailViewId() {
        return R.id.lm_fail;
    }

    /**
     * isLoadEndGone()为true，可以返回0
     * isLoadEndGone()为false，不能返回0
     */
    @Override protected int getLoadEndViewId() {
        return R.id.lm_end;
    }


}
