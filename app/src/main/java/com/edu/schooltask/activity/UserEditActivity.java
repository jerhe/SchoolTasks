package com.edu.schooltask.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.UpdateBgEvent;
import com.edu.schooltask.event.UpdateHeadEvent;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.UserEditItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.PermissionUtil;
import com.edu.schooltask.utils.UserUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import server.api.client.QiniuClient;
import server.api.client.UserClient;
import server.api.event.UploadBGEvent;
import server.api.event.UploadHeadEvent;
import server.api.result.Result;

import static com.edu.schooltask.utils.PermissionUtil.CAMERA_PERMISSION;
import static com.edu.schooltask.utils.PermissionUtil.READ_STORAGE_PERMISSION;

public class UserEditActivity extends BaseActivity{
    @BindView(R.id.ue_head) UserEditItem headItem;
    @BindView(R.id.ue_bg) UserEditItem bgItem;
    @BindView(R.id.ue_name) UserEditItem nameItem;
    @BindView(R.id.ue_sign) UserEditItem signItem;
    @BindView(R.id.ue_school) UserEditItem schoolItem;
    @BindView(R.id.ue_sex) UserEditItem sexItem;
    @BindView(R.id.ue_birth) UserEditItem birthItem;

    @OnClick(R.id.ue_head)
    public void head(){
        pictureMode = 1;
        imageDialog.show();
    }

    @OnClick(R.id.ue_bg)
    public void bg(){
        pictureMode = 2;
        imageDialog.show();
    }

    @OnClick(R.id.ue_name)
    public void name(){
        nameDialog.show();
    }

    @OnClick(R.id.ue_sign)
    public void sign(){
        signDialog.show();
    }

    @OnClick(R.id.ue_school)
    public void school(){
        //schoolDialog.show();
        toastShort("暂不支持修改学校");
    }

    @OnClick(R.id.ue_sex)
    public void sex(){
        sexDialog.show();
    }

    @OnClick(R.id.ue_birth)
    public void birth(){
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(UserEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                String birth = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
                UserClient.updateUserInfo(updateUserInfoResult, birth, 4);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private Result updateUserInfoResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            if(nameDialog != null)
                if(nameDialog.isShowing()){
                    nameDialog.dismiss();
                    KeyBoardUtil.hideKeyBoard(UserEditActivity.this);
                }
            UserInfo updateUserInfo = GsonUtil.toUserInfo(data);
            setInfo(updateUserInfo);
            io.rong.imlib.model.UserInfo userInfo = UserUtil.toRongUserInfo(updateUserInfo);
            RongIM.getInstance().refreshUserInfoCache(userInfo);
            RongIM.getInstance().setCurrentUserInfo(userInfo);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result getHeadUploadKeyResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            UploadKey uploadKey = GsonUtil.toUploadKey(data);
            QiniuClient.uploadHead(headImage, uploadKey);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            progressDialog.dismiss();
            toastShort(error);
        }
    };
    private Result getBgUploadKeyResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            UploadKey uploadKey = GsonUtil.toUploadKey(data);
            QiniuClient.uploadBG(bgImage, uploadKey);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            progressDialog.dismiss();
            toastShort(error);
        }
    };
    private Result refreshHeadResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            String head = (String)data;
            UserInfoWithToken user = UserUtil.getLoginUser();
            user.setHead(head);  //更新本地用户头像地址
            user.updateAll();
            setHead(head);
            io.rong.imlib.model.UserInfo userInfo = UserUtil.toRongUserInfo(user);  //刷新融云本地缓存
            RongIM.getInstance().refreshUserInfoCache(userInfo);
            RongIM.getInstance().setCurrentUserInfo(userInfo);  //修改当前用户头像
            EventBus.getDefault().post(new UpdateHeadEvent());
            toastShort("上传头像成功");
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort("上传头像失败");
        }
    };
    private Result refreshBgResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            String bg = (String)data;
            UserInfoWithToken user = UserUtil.getLoginUser();
            user.setBg(bg);    //更新本地用户背景地址
            user.updateAll();
            setBg(bg);
            EventBus.getDefault().post(new UpdateBgEvent());
            toastShort("上传背景成功");
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort("上传背景失败");
        }
    };

    Uri imageUri;
    int pictureMode = 0;    //1:head 2:bg
    File headImage;
    File bgImage;

    DialogPlus imageDialog;
    DialogPlus nameDialog;
    DialogPlus signDialog;
    DialogPlus schoolDialog;
    DialogPlus sexDialog;
    private ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_user_edit;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        initDialog();
        UserInfoWithToken user = UserUtil.getLoginUser();
        setInfo(user);
        setHead(user.getHead());
        setBg(user.getBg());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        switch (requestCode) {
            case CAMERA_REQUEST:
                clipPhoto();
                break;
            case ALBUM_PICK_REQUEST:
                imageUri = data.getData();
                clipPhoto();
                break;
            case UCrop.REQUEST_CROP:
                if(data!=null){
                    imageUri = UCrop.getOutput(data);
                    progressDialog = ProgressDialog.show(UserEditActivity.this, "", "正在上传...", true, false);
                    if(pictureMode == 1){
                        headImage = new File(imageUri.getPath());
                        QiniuClient.getHeadUploadKey(getHeadUploadKeyResult);
                    }
                    else{
                        bgImage = new File(imageUri.getPath());
                        QiniuClient.getBGUploadKey(getBgUploadKeyResult);
                    }
                }
                break;
        }
    }

    private void initDialog(){
        imageDialog = DialogUtil.createListDialog(this, "选择图片", new DialogUtil.ListItemClickListener() {
            @Override
            public void onItemClick(int position, String item) {
                switch (position){
                    case 0:
                        if(PermissionUtil.checkCameraPermission(UserEditActivity.this)) imageUri = openCamera();
                        break;
                    case 1:
                        if(PermissionUtil.checkReadStoragePermission(UserEditActivity.this)) openAlbumActivity();
                        break;
                }
            }
        }, "拍照", "从相册选择");

        nameDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                if(input.length() == 0) {
                    toastShort("请输入昵称");
                    return;
                }
                if(UserUtil.getLoginUser().getName().equals(input))return;
                UserClient.updateUserInfo(updateUserInfoResult, input, UserClient.UPDATE_NAME);
            }
        }, "修改昵称" ,UserUtil.getLoginUser().getName());

        signDialog = DialogUtil.createInputSignDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                dialogPlus.dismiss();
                if(UserUtil.getLoginUser().getSign().equals(input))return;
                UserClient.updateUserInfo(updateUserInfoResult, input, UserClient.UPDATE_SIGN);
            }
        }, UserUtil.getLoginUser().getSign());

        schoolDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                if(input.length() == 0) {
                    toastShort("请输入学校");
                    return;
                }
                dialogPlus.dismiss();
                if(UserUtil.getLoginUser().getSchool().equals(input))return;
                UserClient.updateUserInfo(updateUserInfoResult, input, UserClient.UPDATE_SCHOOL);
            }
        }, "修改学校" ,UserUtil.getLoginUser().getSchool());
        View view = schoolDialog.getHolderView();
        EditText editText = (EditText) view.findViewById(R.id.di_input);
        editText.addTextChangedListener(new SchoolAutoComplement(editText, mDataCache.getSchool()));

        sexDialog = DialogUtil.createListDialog(this, "请选择性别", new DialogUtil.ListItemClickListener() {
            @Override
            public void onItemClick(int position, String item) {
                UserClient.updateUserInfo(updateUserInfoResult, item, UserClient.UPDATE_SEX);
            }
        }, "男", "女");
    }

    //裁剪图片
    public void clipPhoto() {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle("裁剪");
        if(pictureMode==1)options.setOvalDimmedLayer(true);
        options.setToolbarColor(Color.parseColor("#1B9DFF"));
        options.setStatusBarColor(Color.parseColor("#1B9DFF"));
        options.setActiveWidgetColor(Color.parseColor("#1B9DFF"));
        options.setLogoColor(Color.BLACK);
        options.setCropGridColor(Color.parseColor("#00000000"));
        Uri destinationUri = Uri.fromFile(new File(getCacheDir().getPath()+"/temp.jpg"));
        UCrop uCrop = UCrop.of(imageUri,destinationUri);
        uCrop.withOptions(options);
        if(pictureMode==1){ //头像的裁剪框是正方形
            uCrop.withAspectRatio(1, 1);
            uCrop.withMaxResultSize(200, 200);
        }
        else{   //背景的裁剪框是16:10
            uCrop.withAspectRatio(16, 10);
            uCrop.withMaxResultSize(480, 300);
        }
        uCrop.start(UserEditActivity.this);
    }

    public void setInfo(UserInfo user){
        nameItem.setTextText(user.getName());
        String sign = user.getSign();
        if(sign != null) signItem.setTextText(sign);
        schoolItem.setTextText(user.getSchool());
        switch (user.getSex()){
            case 0:
                sexItem.setTextText("男");
                break;
            case 1:
                sexItem.setTextText("女");
                break;
        }
        birthItem.setTextText(user.getBirth());
    }

    private void setHead(String head){
        UserUtil.setHead(UserEditActivity.this, head, headItem.getHeadView());
    }

    private void setBg(String bg){
        UserUtil.setBackground(UserEditActivity.this, bg, bgItem.getImageView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadBG(UploadBGEvent event){
        if (event.isOk()){
            String url = event.getUrl();    //背景地址
            UserClient.refreshBg(refreshBgResult, url);  //更新服务端背景地址
        }
        else{
            progressDialog.dismiss();
            toastShort("上传背景失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadHead(UploadHeadEvent event){
        if (event.isOk()){
            String url = event.getUrl();
            UserClient.refreshHead(refreshHeadResult, url);    //更新服务端头像地址
        }
        else{
            progressDialog.dismiss();
            toastShort("上传头像失败");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_STORAGE_PERMISSION:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openAlbumActivity();
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
}
