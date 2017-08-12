package com.edu.schooltask.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideCacheUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.UserEditItem;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.qiniu.GetBGUploadKeyEvent;
import server.api.qiniu.GetHeadUploadKeyEvent;
import server.api.user.UpdateUserInfoEvent;
import server.api.qiniu.UploadBGEvent;
import server.api.qiniu.UploadHeadEvent;

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
        DialogUtil.createListDialog(UserEditActivity.this, imageSelectAdapter, itemClickListener).show();
    }
    @OnClick(R.id.ue_bg)
    public void bg(){
        pictureMode = 2;
        DialogUtil.createListDialog(UserEditActivity.this, imageSelectAdapter, itemClickListener).show();
    }
    @OnClick(R.id.ue_name)
    public void name(){
        nameDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                if(input.length() == 0) {
                    toastShort("请输入昵称");
                    return;
                }
                if(UserUtil.getLoginUser().getName().equals(input))return;
                SchoolTask.updateUserInfo(input, 0);
            }
        }, "修改昵称" ,UserUtil.getLoginUser().getName());
        nameDialog.show();
    }
    @OnClick(R.id.ue_sign)
    public void sign(){
        DialogUtil.createInputMultilineDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                dialogPlus.dismiss();
                if(UserUtil.getLoginUser().getSign().equals(input))return;
                SchoolTask.updateUserInfo(input, 1);
            }
        }, "修改简介", UserUtil.getLoginUser().getSign()).show();
    }
    @OnClick(R.id.ue_school)
    public void school(){
        schoolDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
            @Override
            public void onInputClick(DialogPlus dialogPlus, String input) {
                if(input.length() == 0) {
                    toastShort("请输入学校");
                    return;
                }
                dialogPlus.dismiss();
                if(UserUtil.getLoginUser().getSchool().equals(input))return;
                SchoolTask.updateUserInfo(input, 2);
            }
        }, "修改学校" ,UserUtil.getLoginUser().getSchool());
        View view = schoolDialog.getHolderView();
        EditText editText = (EditText) view.findViewById(R.id.di_input);
        editText.addTextChangedListener(
                new SchoolAutoComplement(editText, mDataCache.getSchool()));
        schoolDialog.show();
    }
    @OnClick(R.id.ue_sex)
    public void sex(){
        DialogUtil.createListDialog(UserEditActivity.this, sexSelectAdapter, new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                SchoolTask.updateUserInfo(sexSelectList.get(position), 3);
                dialog.dismiss();
            }
        }).show();
    }
    @OnClick(R.id.ue_birth)
    public void birth(){
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(UserEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                String birth = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
                SchoolTask.updateUserInfo(birth, 4);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    List<String> imageSelectList = new ArrayList<>();
    BaseAdapter imageSelectAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return imageSelectList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageSelectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(UserEditActivity.this).inflate(R.layout.item_text,null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(imageSelectList.get(position));
            return view;
        }
    };
    Uri imageUri;

    List<String> sexSelectList = new ArrayList<>();
    BaseAdapter sexSelectAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return sexSelectList.size();
        }

        @Override
        public Object getItem(int position) {
            return sexSelectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(UserEditActivity.this).inflate(R.layout.item_text,null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setGravity(Gravity.CENTER);
            textView.setText(sexSelectList.get(position));
            return view;
        }
    };

    final static int CAMERA = 0;
    final static int PICTURE = 1;
    final static int CUT = 2;
    int pictureMode = 0;
    File headImage;
    File bgImage;

    DialogPlus nameDialog;
    DialogPlus schoolDialog;

    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
            dialog.dismiss();
            switch (position){
                case 0: //拍照
                    File outputImage = new File(getExternalCacheDir(), "camera_image.jpg");
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    try {
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(UserEditActivity.this, "com.edu.camera.fileprovider", outputImage);
                    } else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, CAMERA);
                    break;
                case 1: //图库
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(albumIntent, PICTURE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        imageSelectList.add("拍照");
        imageSelectList.add("从相册中选择");

        sexSelectList.add("男");
        sexSelectList.add("女");

        UserInfo user = UserUtil.getLoginUser();
        setInfo(user);
        setHead(user);
        setBg(user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA:
                if (resultCode == RESULT_OK) {
                    clipPhoto();
                }
                break;
            case PICTURE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    clipPhoto();
                }
                break;
            case UCrop.REQUEST_CROP:
                if(data!=null){
                    imageUri = UCrop.getOutput(data);
                    if(pictureMode == 1){
                        headImage = new File(imageUri.getPath());
                        SchoolTask.getHeadUploadKey();
                    }
                    else{
                        bgImage = new File(imageUri.getPath());
                        SchoolTask.getBGUploadKey();
                    }
                }
                break;
        }
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
        if(sign != null) {
            if (sign.length() > 8) sign = sign.substring(0, 8) + "...";
            signItem.setTextText(sign);
        }
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

    private void setHead(UserInfo user){
        Glide.clear(headItem.getHeadView());
        GlideUtil.setHead(UserEditActivity.this, user.getUserId(), headItem.getHeadView(), false);
    }

    private void setBg(UserInfo user){
        Glide.clear(bgItem.getImageView());
        GlideUtil.setBackground(UserEditActivity.this, user.getUserId(), bgItem.getImageView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadBG(UploadBGEvent event){
        if (event.isOk()){
            toastShort("上传背景成功");
            mDataCache.saveData("bg",new Random().nextInt(999));
            GlideCacheUtil.getInstance().clearImageAllCache(this);
            setBg(UserUtil.getLoginUser());
        }
        else{
            toastShort("上传背景失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadHead(UploadHeadEvent event){
        if (event.isOk()){
            toastShort("上传头像成功");
            mDataCache.saveData("head",new Random().nextInt(999));
            GlideCacheUtil.getInstance().clearImageAllCache(this);
            setHead(UserUtil.getLoginUser());
        }
        else{
            toastShort("上传头像失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfoEvent event){
        if(event.isOk()){
            if(nameDialog != null)
                if(nameDialog.isShowing()){
                    nameDialog.dismiss();
                }
            UserInfoBase updateUserInfo = GsonUtil.toUserInfoBase(event.getData());
            UserInfo user = UserUtil.getLoginUser().setUserInfo(updateUserInfo);
            UserUtil.saveLoginUser(user);
            setInfo(user);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetHeadUploadKey(GetHeadUploadKeyEvent event){
        if(event.isOk()){
            UploadKey uploadKey = GsonUtil.toUploadKey(event.getData());
            SchoolTask.uploadHead(headImage, uploadKey);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetBGUploadKey(GetBGUploadKeyEvent event){
        if(event.isOk()){
            UploadKey uploadKey = GsonUtil.toUploadKey(event.getData());
            SchoolTask.uploadBG(bgImage, uploadKey);
        }
        else{
            toastShort(event.getError());
        }
    }
}
