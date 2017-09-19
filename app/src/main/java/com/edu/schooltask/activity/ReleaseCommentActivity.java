package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.base.BaseReleaseActivity;
import com.edu.schooltask.view.ContentView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import id.zelory.compressor.Compressor;
import server.api.client.DynamicClient;
import server.api.client.QiniuClient;
import server.api.client.TaskClient;
import server.api.result.Result;

import static server.api.SchoolTask.IMAGE;

public class ReleaseCommentActivity extends BaseReleaseActivity {
    public final static int RELEASE_TYPE_ORDER = 0;
    public final static int RELEASE_TYPE_DYNAMIC = 1;

    @BindView(R.id.rc_comment) ContentView contentView;

    private Result getUploadKeyResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            File image = tempFiles.get(0);
            String imageUrl = buildImageUrl(releaseType);
            comment.setImage(IMAGE + imageUrl);
            QiniuClient.uploadCommentImage(GsonUtil.toUploadKey(data), imageUrl, image);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            tempFiles.clear();
            progressDialog.dismiss();
            toastShort(error);
        }

        private String buildImageUrl(int releaseType){
            StringBuilder sb = new StringBuilder();
            sb.append("comment/");
            if(releaseType == RELEASE_TYPE_ORDER) sb.append("order/").append(orderId);
            if(releaseType == RELEASE_TYPE_DYNAMIC) sb.append("dynamic/").append(dynamicId);
            sb.append(System.currentTimeMillis()).append(".png");
            return sb.toString();
        }
    };
    private Result commentResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            ReleaseCommentActivity.this.setResult(RESULT_OK);
            toastShort("发送成功");
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    private int releaseType;
    private String orderId;
    private int dynamicId;
    private Comment comment = new Comment();

    @Override
    public int getLayout() {
        return R.layout.activity_release_comment;
    }

    @Override
    public void init() {
        initDialog(1);
        Intent intent = getIntent();
        comment.setParentId(intent.getIntExtra("commentId", 0));
        String toName = intent.getStringExtra("toName");
        boolean isParent = intent.getBooleanExtra("isParent", true);
        if(StringUtil.isEmpty(toName)) toName = "";
        else contentView.setHint("回复 " + toName);
        //回复顶层评论显示回复昵称但不设置回复昵称，回复昵称仅用于子回复中显示回复对象
        if (isParent) toName = "";
        comment.setToName(toName);
        releaseType = intent.getIntExtra("releaseType", 0);
        switch (releaseType){
            case RELEASE_TYPE_ORDER:
                orderId = intent.getStringExtra("orderId");
                break;
            case RELEASE_TYPE_DYNAMIC:
                dynamicId = intent.getIntExtra("dynamicId", 0);
                break;
            default:
                throw new RuntimeException("must has release type!");
        }
    }

    @Override
    public int createMenu() {
        return R.menu.comment;
    }

    @Override
    public boolean menuClick(int menuId) {
        release();
        return true;
    }

    @Override
    protected boolean exit() {
        return StringUtil.isEmpty(contentView.getText());
    }

    @Override
    protected void uploadSuccess() {
        if(releaseType == RELEASE_TYPE_ORDER) TaskClient.comment(commentResult, orderId, comment);
        else DynamicClient.comment(commentResult, dynamicId, comment);
    }

    @Override
    protected void uploadFailed() {
        toastShort("发送失败");
    }

    private void release(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user == null){
            EventBus.getDefault().post(new UnloginEvent());
            return;
        }
        String comment = contentView.getText();
        int imageSize = paths.size();
        if(StringUtil.isEmpty(comment) && imageSize == 0){
            toastShort("请输入评论或选择图片");
            return;
        }
        this.comment.setComment(comment);
        this.comment.setImage(imageSize == 0 ? "" : paths.get(0));
        KeyBoardUtil.hideKeyBoard(this);
        progressDialog = ProgressDialog.show(this, "", "发送中...", true, false);
        if(imageSize == 0){
            if(releaseType == RELEASE_TYPE_ORDER) TaskClient.comment(commentResult, orderId, this.comment);
            else DynamicClient.comment(commentResult, dynamicId, this.comment);
        }
        else{
            tempFiles.add(new Compressor.Builder(ReleaseCommentActivity.this)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setQuality(100).build().compressToFile(new File(paths.get(0))));
            QiniuClient.getUploadKey(getUploadKeyResult);
        }
    }

}
