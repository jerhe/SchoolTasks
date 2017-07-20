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
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideCacheUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.UserEditItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import server.api.SchoolTask;
import server.api.qiniu.GetBGUploadKeyEvent;
import server.api.qiniu.GetHeadUploadKeyEvent;
import server.api.user.UpdateUserInfoEvent;
import server.api.qiniu.UploadBGEvent;
import server.api.qiniu.UploadHeadEvent;

public class UserEditActivity extends BaseActivity implements View.OnClickListener{
    UserEditItem headItem;
    UserEditItem bgItem;
    UserEditItem nameItem;
    UserEditItem signItem;
    UserEditItem schoolItem;
    UserEditItem sexItem;
    UserEditItem birthItem;

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
        EventBus.getDefault().register(this);
        headItem = getView(R.id.ue_head);
        bgItem = getView(R.id.ue_bg);
        nameItem = getView(R.id.ue_name);
        signItem = getView(R.id.ue_sign);
        schoolItem = getView(R.id.ue_school);
        sexItem = getView(R.id.ue_sex);
        birthItem = getView(R.id.ue_birth);

        headItem.setOnClickListener(this);
        bgItem.setOnClickListener(this);
        nameItem.setOnClickListener(this);
        signItem.setOnClickListener(this);
        schoolItem.setOnClickListener(this);
        sexItem.setOnClickListener(this);
        birthItem.setOnClickListener(this);

        imageSelectList.add("拍照");
        imageSelectList.add("从相册中选择");

        sexSelectList.add("男");
        sexSelectList.add("女");

        User user = mDataCache.getUser();
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

    public void setInfo(User user){
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

    private void setHead(User user){
        Glide.clear(headItem.getHeadView());
        GlideUtil.setHead(UserEditActivity.this, user.getUserId(), headItem.getHeadView(), false);
    }

    private void setBg(User user){
        Glide.clear(bgItem.getImageView());
        GlideUtil.setBackground(UserEditActivity.this, user.getUserId(), bgItem.getImageView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadBG(UploadBGEvent event){
        if (event.isOk()){
            toastShort("上传背景成功");
            mDataCache.saveData("bg",new Random().nextInt(999));
            GlideCacheUtil.getInstance().clearImageAllCache(this);
            setBg(mDataCache.getUser());
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
            setHead(mDataCache.getUser());
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
            UserBaseInfo updateUserInfo = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UserBaseInfo>(){}.getType());
            User user = mDataCache.getUser().setUserInfo(updateUserInfo);
            mDataCache.saveUser(user);
            setInfo(user);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetHeadUploadKey(GetHeadUploadKeyEvent event){
        if(event.isOk()){
            UploadKey uploadKey = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UploadKey>(){}.getType());
            SchoolTask.uploadHead(headImage, uploadKey);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetBGUploadKey(GetBGUploadKeyEvent event){
        if(event.isOk()){
            UploadKey uploadKey = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UploadKey>(){}.getType());
            SchoolTask.uploadBG(bgImage, uploadKey);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Override
    public void onClick(View v) {
        final User user = mDataCache.getUser();
        switch (v.getId()){
            case R.id.ue_head:
                pictureMode = 1;
                DialogUtil.createListDialog(UserEditActivity.this, imageSelectAdapter, itemClickListener).show();
                break;
            case R.id.ue_bg:
                pictureMode = 2;
                DialogUtil.createListDialog(UserEditActivity.this, imageSelectAdapter, itemClickListener).show();
                break;
            case R.id.ue_name:
                nameDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
                    @Override
                    public void onInputClick(DialogPlus dialogPlus, String input) {
                        if(input.length() == 0) {
                            toastShort("请输入昵称");
                            return;
                        }
                        if(user.getName().equals(input))return;
                        SchoolTask.updateUserInfo(input, 0);
                    }
                }, "修改昵称" ,mDataCache.getUser().getName());
                nameDialog.show();
                break;
            case R.id.ue_sign:
                DialogUtil.createInputMultilineDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
                    @Override
                    public void onInputClick(DialogPlus dialogPlus, String input) {
                        dialogPlus.dismiss();
                        if(user.getSign().equals(input))return;
                        SchoolTask.updateUserInfo(input, 1);
                    }
                }, "修改简介", mDataCache.getUser().getSign()).show();
                break;
            case R.id.ue_school:
                schoolDialog = DialogUtil.createInputDialog(UserEditActivity.this, new DialogUtil.OnInputClickListener() {
                    @Override
                    public void onInputClick(DialogPlus dialogPlus, String input) {
                        if(input.length() == 0) {
                            toastShort("请输入学校");
                            return;
                        }
                        dialogPlus.dismiss();
                        if(user.getSchool().equals(input))return;
                        SchoolTask.updateUserInfo(input, 2);
                    }
                }, "修改学校" ,mDataCache.getUser().getSchool());
                View view = schoolDialog.getHolderView();
                EditText editText = (EditText) view.findViewById(R.id.di_input);
                TextUtil.setSchoolWatcher(UserEditActivity.this, editText, mDataCache);
                schoolDialog.show();
                break;
            case R.id.ue_sex:
                DialogUtil.createListDialog(UserEditActivity.this, sexSelectAdapter, new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        SchoolTask.updateUserInfo(sexSelectList.get(position), 3);
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.ue_birth:
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
                break;
        }
    }
}
