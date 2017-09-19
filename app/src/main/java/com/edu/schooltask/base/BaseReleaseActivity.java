package com.edu.schooltask.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.PermissionUtil;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import server.api.event.UploadImageEvent;

import static com.edu.schooltask.utils.PermissionUtil.CAMERA_PERMISSION;
import static com.edu.schooltask.utils.PermissionUtil.READ_STORAGE_PERMISSION;

/**
 * Created by 夜夜通宵 on 2017/9/13.
 */

public abstract class BaseReleaseActivity extends BaseActivity {

    @BindView(R.id.release_select_image_btn) protected ImageTextView selectImageBtn;
    @BindView(R.id.release_irv) protected ImageRecyclerView imageRecyclerView;
    @BindView(R.id.release_clear) protected TextView clearBtn;

    protected List<File> tempFiles = new ArrayList<>();   //压缩图片
    protected ArrayList<String> paths = new ArrayList<>();   //选中图片
    protected Uri imageUri;   //拍照图片
    protected DialogPlus imageDialog;

    @OnClick(R.id.release_select_image_btn)
    public void selectImage(){
        KeyBoardUtil.hideKeyBoard(this);
        imageDialog.show();
    }

    @OnClick(R.id.release_clear)
    public void clearImage(){
        paths.clear();
        imageRecyclerView.clear();
        clearBtn.setVisibility(View.GONE);
    }

    protected DialogPlus exitDialog;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
            @Override
            public void onImageClick(int position, ImageItem imageItem) {
                openImageActivity(imageRecyclerView.get(), position, true);
            }
        });
        imageRecyclerView.setSpaceClickListener(new ImageRecyclerView.SpaceClickListener() {
            @Override
            public void OnSpaceClick() {
                imageDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!exit() || paths.size() != 0) exitDialog.show();
        else finish();
    }

    protected void initDialog(final int maxImageSize){
        exitDialog = DialogUtil.createTextDialog(this, "提示", "确定要返回吗?", "", new DialogUtil.OnClickListener() {
            @Override
            public void onClick(DialogPlus dialogPlus) {
                finish();
            }
        });
        imageDialog = DialogUtil.createListDialog(this, "选择图片", new DialogUtil.ListItemClickListener() {
            @Override
            public void onItemClick(int position, String item) {
                switch (position){
                    case 0:
                        if(paths.size() >= 9) {
                            toastShort("最多只能上传" + maxImageSize + "张图片");
                            return;
                        }
                        if(PermissionUtil.checkCameraPermission(BaseReleaseActivity.this))
                            imageUri = openCamera();
                        break;
                    case 1:
                        if(PermissionUtil.checkReadStoragePermission(BaseReleaseActivity.this))
                            openImageSelectActivity(paths, maxImageSize);
                        break;
                }
            }
        }, "拍照", "从相册选择");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteImage(DeleteImageEvent event){
        paths.remove(event.getIndex());
        imageRecyclerView.remove(event.getIndex());
        imageRecyclerView.notifyDataSetChanged();
        refreshClearVisible();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadImageEvent(UploadImageEvent event){
        if(event.isOk()){
            int index = event.getIndex();
            progressDialog.setMessage("正在上传图片 " + index + "/" + tempFiles.size());
            if(index == tempFiles.size() - 1){  //全部图片上传完成
                uploadSuccess();
                tempFiles.clear();
            }
        }
        else{   //上传失败
            progressDialog.dismiss();
            tempFiles.clear();
            uploadFailed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_IMAGE_REQUEST:
                imageRecyclerView.clear();
                paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                List<ImageItem> imageItems = new ArrayList<>();
                for (String path : paths) {
                    imageItems.add(new ImageItem(1, path));
                }
                imageRecyclerView.add(imageItems);
                refreshClearVisible();
                break;
            case CAMERA_REQUEST:
                if(imageUri == null) return;
                String path = imageUri.getPath();
                paths.add(path);
                imageRecyclerView.add(new ImageItem(1, path));
                refreshClearVisible();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_STORAGE_PERMISSION:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openImageSelectActivity(paths, 9);
                else toastShort("获取读权限失败！请在设置中开启权限");
                break;
            case CAMERA_PERMISSION:
                if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera();
                else toastShort("获取相机权限失败！请在设置中开启权限");
                break;
            default:
                break;
        }
    }

    private void refreshClearVisible(){
        if(imageRecyclerView.get().size() > 0) clearBtn.setVisibility(View.VISIBLE);
        else clearBtn.setVisibility(View.GONE);
    }

    public void openImageActivity(List<ImageItem> images, int position, boolean editable){
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("editable", editable);
        intent.putExtra("index", position);
        List<String> imageList = new ArrayList<>();
        for(ImageItem imageItem : images) imageList.add(imageItem.getPath());
        intent.putExtra("images", (Serializable) imageList);
        startActivity(intent);
    }

    protected abstract boolean exit();
    protected abstract void uploadSuccess();
    protected abstract void uploadFailed();
}
