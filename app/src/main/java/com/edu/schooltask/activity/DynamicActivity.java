package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.event.DeleteDynamicEvent;
import com.edu.schooltask.event.LikeChangedEvent;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.base.BaseCommentActivity;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.view.DynamicView;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.HeadRecyclerView;
import com.edu.schooltask.view.recyclerview.MenuRecyclerView;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.DynamicClient;
import server.api.result.Result;

public class DynamicActivity extends BaseCommentActivity{

    @BindView(R.id.dynamic_dv) DynamicView dynamicView;
    @BindView(R.id.dynamic_like_btn) ImageTextView likeBtn;
    @BindView(R.id.dynamic_like_head_count) TextView likeText;
    @BindView(R.id.dynamic_head_rv) HeadRecyclerView headRV;

    @OnClick(R.id.dynamic_like_layout)
    public void like(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if (user == null){
            EventBus.getDefault().post(new UnloginEvent());
            return;
        }
        dynamic.setLike(!dynamic.isLike());
        setLikeBtn(true);
        dynamic.setLikeCount(dynamic.getLikeCount() + (dynamic.isLike() ? 1 : -1));
        likeText.setText(dynamic.getLikeCount() + "人喜欢");
        List<String> headList = headRV.get();
        if(dynamic.isLike()){   //喜欢添加头像
            headRV.add(0, user.getHead());
        }
        else{   //取消喜欢移除头像
            for(String head : headList){
                if(StringUtil.isEmpty(head) && StringUtil.isEmpty(user.getHead())
                        || head.equals(user.getHead())){
                    headRV.remove(headList.indexOf(head));
                    headRV.notifyDataSetChanged();
                    break;
                }
            }
        }
        DynamicClient.dynamicLike(dynamicLikeResult, dynamic.getId(), dynamic.isLike());
    }

    @OnClick(R.id.dynamic_like_head_count)
    public void likeTextClick(){
        openActivity(LikeListActivity.class, "dynamicId", dynamic.getId());
    }

    private Result getDynamicResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            boolean isLike = dynamic.isLike();
            dynamic = GsonUtil.toDynamic(data);
            dynamic.setLike(isLike);
            if (!dynamic.isAvailable()){
                toastShort("动态已删除");
                finish();
                return;
            }
            dynamicView.setAll(dynamic, true);
            countView.setCount(dynamic.getCommentCount(), dynamic.getLookCount());
            likeText.setText(dynamic.getLikeCount() + "人喜欢");
            List<String> headList = dynamic.getHeadList();
            headRV.clear();
            headRV.add(headList);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
            if(code == 803){
                finish();
            }
        }
    };
    private Result dynamicLikeResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            EventBus.getDefault().post(new LikeChangedEvent(dynamic.getId(), dynamic.isLike()));
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
            if(code == 803){
                finish();
            }
        }
    };
    private Result deleteDynamicResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort("删除成功");
            EventBus.getDefault().post(new DeleteDynamicEvent(dynamic.getId()));
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    private Dynamic dynamic;
    private Animation likeAnimation;
    private PopupWindow moreMenu;
    private MenuRecyclerView moreRV;

    @Override
    public int getLayout() {
        return R.layout.activity_dynamic;
    }

    @Override
    public void init() {
        likeAnimation = AnimationUtils.loadAnimation(this, R.anim.like);
        likeAnimation.setInterpolator(new OvershootInterpolator());
        Intent intent = getIntent();
        dynamic = (Dynamic) intent.getSerializableExtra("dynamic");
        //dynamicView.setAll(dynamic, true);    防止多图自动滚动
        dynamicView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogUtil.createListDialog(DynamicActivity.this, "动态", new DialogUtil.ListItemClickListener() {
                    @Override
                    public void onItemClick(int position, String item) {
                        switch (position){
                            case 0:
                                StringUtil.copy(DynamicActivity.this, dynamic.getContent());
                                toastShort("已经复制到粘贴板");
                                break;
                            case 1:
                                openDynamicCommentActivity(dynamic.getId());
                                break;
                        }
                    }
                }, "复制", "评论").show();
                return true;
            }
        });
        headRV.setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                openActivity(LikeListActivity.class, "dynamicId", dynamic.getId());
            }
        });
        initMoreMenu();
        setLikeBtn(false);
        super.init();
    }

    @Override
    public int createMenu() {
        return R.menu.more;
    }

    @Override
    public boolean menuClick(int menuId) {
        moreMenu.showAsDropDown(toolbar, toolbar.getWidth() -  DensityUtil.dipToPx(this, 105),
                DensityUtil.dipToPx(this, 5));
        return true;
    }

    private void initMoreMenu(){
        moreMenu = new PopupWindow(
                DensityUtil.dipToPx(this, 100), LinearLayout.LayoutParams.WRAP_CONTENT);
        moreRV = DialogUtil.createPopupMenu(this, moreMenu, new ItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String s) {
                moreMenu.dismiss();
                switch (position){
                    case 0: //分享
                        break;
                    case 1: //举报
                        break;
                    case 2: //删除
                        deleteDynamic();
                        break;
                }
            }
        });
        moreRV.add("分享");
        moreRV.add("举报");
        UserInfoWithToken user = UserUtil.getLoginUser();
        if (user != null && dynamic.getUserId().equals(user.getUserId()))
            moreRV.add("删除");
    }

    @Override
    public int[] defaultCount() {
        return new int[]{dynamic.getCommentCount(), dynamic.getLookCount()};
    }

    @Override
    public void comment() {
        if (!UserUtil.hasLogin()){
            EventBus.getDefault().post(new UnloginEvent());
            return;
        }
        openDynamicCommentActivity(dynamic.getId());
    }

    @Override
    public void getCommentList(Result result, int page) {
        DynamicClient.getCommentList(result, dynamic.getId(), countView.getOrder(), page);
    }

    @Override
    public void onCommentClick(Comment comment) {
        openDynamicCommentActivity(dynamic.getId(), comment.getId(), comment.getUserInfo().getName(), true);
    }

    @Override
    public void openReply(Comment comment) {
        openReplyActivity(COMMENT_DYNAMIC, comment.getId(), null, dynamic.getId(),
                comment.getReplyCount(), comment.getUserInfo().getName());
    }


    @Override
    public void refresh(){
        DynamicClient.getDynamic(getDynamicResult, dynamic.getId());
        commentRecyclerView.refresh();
    }


    public void setLikeBtn(boolean animate){
        if(dynamic.isLike()){
            if(animate) likeBtn.startIconAnimation(likeAnimation);
            likeBtn.setIcon(R.drawable.ic_like_light);
            likeBtn.setText("取消喜欢");
            likeBtn.setTextColor(Color.RED);
        }
        else{
            likeBtn.setIcon(R.drawable.ic_like);
            likeBtn.setText("喜欢");
            likeBtn.setTextColor(getResources().getColor(R.color.fontColor));
        }
    }

    private void deleteDynamic(){
        DialogUtil.createTextDialog(this, "提示", "确定要删除动态吗？", "", new DialogUtil.OnClickListener() {
            @Override
            public void onClick(DialogPlus dialogPlus) {
                DynamicClient.delete(deleteDynamicResult, dynamic.getId());
            }
        }).show();
    }
}
